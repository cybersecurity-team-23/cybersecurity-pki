package com.example.pki.service;

import com.example.pki.dto.CertificateDto;
import com.example.pki.dto.CertificateType;
import com.example.pki.dto.CreateCertificateDto;
import com.example.pki.dto.X500NameDto;
import com.example.pki.repository.KeyStoreRepository;
import com.example.pki.repository.PasswordRepository;
import com.example.pki.repository.PrivateKeyRepository;
import com.example.pki.util.DateConverter;
import com.example.pki.util.DateRange;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateService {
    private final KeyStoreRepository keyStoreRepository;
    private final PasswordRepository passwordRepository;
    private final PrivateKeyRepository privateKeyRepository;

    public CertificateService(KeyStoreRepository keyStoreRepository, PasswordRepository passwordRepository,
                              PrivateKeyRepository privateKeyRepository) {
        this.keyStoreRepository = keyStoreRepository;
        keyStoreRepository
                .readKeyStore(
                        KeyStoreRepository.keyStoreFilePath,
                        passwordRepository.getPassword(KeyStoreRepository.keyStoreName).toCharArray()
                );

        this.passwordRepository = passwordRepository;
        this.privateKeyRepository = privateKeyRepository;
    }

    public X509Certificate getIssuer(X509Certificate certificate) {
        for (Certificate keyStoreCertificate : keyStoreRepository.getAllCertificates()) {
            if (!(keyStoreCertificate instanceof X509Certificate x509Certificate))
                continue;

            if (x509Certificate.getSubjectX500Principal().equals(certificate.getIssuerX500Principal()))
                return x509Certificate;
        }

        return null;
    }

    public boolean isRoot(X509Certificate certificate) {
        try {
            certificate.verify(certificate.getPublicKey());
            return certificate.getSubjectX500Principal().equals(certificate.getIssuerX500Principal());
        } catch (CertificateException | NoSuchAlgorithmException | SignatureException | InvalidKeyException |
                 NoSuchProviderException e) {
            return false;
        }
    }

    private X509Certificate getRoot(X509Certificate certificate) {
        X509Certificate issuerCertificate = getIssuer(certificate);
        if (issuerCertificate == null)
            return null;

        if (isRoot(issuerCertificate))
            return issuerCertificate;
        else
            return getRoot(issuerCertificate);
    }

    public Set<X509Certificate> getCertificatesSignedBy(X509Certificate certificate) {
        Set<X509Certificate> certificates = new HashSet<>();
        for (Certificate keyStoreCertificate : keyStoreRepository.getAllCertificates()) {
            if (!(keyStoreCertificate instanceof X509Certificate x509Certificate))
                continue;

            if (
                    // To prevent infinite recursing when finding certificates signed by root, since it is self-signed
                    !x509Certificate.equals(certificate) &&
                    x509Certificate.getIssuerX500Principal().equals(certificate.getSubjectX500Principal())
            )
                certificates.add(x509Certificate);
        }

        return certificates;
    }

    public boolean isEndEntity(X509Certificate certificate) {
        boolean[] keyUsages = certificate.getKeyUsage();
        if (keyUsages == null)
            return true;

        return !certificate.getKeyUsage()[5];
    }

    private CertificateDto formCertificateTree(X509Certificate rootCertificate) throws CertificateEncodingException {
        JcaX509CertificateHolder jcaX509CertificateHolder = new JcaX509CertificateHolder(rootCertificate);
        CertificateDto certificateTree =
                new CertificateDto(
                        rootCertificate,
                        jcaX509CertificateHolder.getIssuer(),
                        jcaX509CertificateHolder.getSubject(),
                        isEndEntity(rootCertificate),
                        isRoot(rootCertificate)
                );
        Set<X509Certificate> certificatesSignedByRoot = getCertificatesSignedBy(rootCertificate);
        if (certificatesSignedByRoot.isEmpty())
            return certificateTree;

        for (X509Certificate certificate : certificatesSignedByRoot)
            certificateTree.getChildren().add(formCertificateTree(certificate));

        return certificateTree;
    }

    public CertificateDto getCertificateTree() throws CertificateEncodingException {
        Optional<X509Certificate> randomCertificate =
                keyStoreRepository
                        .getAllCertificates()
                        .stream()
                        .filter(certificate -> certificate instanceof X509Certificate)
                        .map(certificate -> (X509Certificate) certificate)
                        .findAny();
        if (randomCertificate.isEmpty())
            return null;

        X509Certificate rootCertificate = getRoot(randomCertificate.get());
        if (rootCertificate == null)
            throw new RuntimeException("Root certificate not found.");

        return formCertificateTree(rootCertificate);
    }

    public boolean isCertValid(String alias) {
        X509Certificate currentX509 =
                (X509Certificate) keyStoreRepository.readCertificate(
                        KeyStoreRepository.keyStoreFilePath,
                        passwordRepository.getPassword(KeyStoreRepository.keyStoreName),
                        alias
                );
        if (currentX509 == null)
            return false;

        X509Certificate parentX509 = getIssuer(currentX509);
        while (!isRoot(currentX509)) {
            try {
                currentX509.checkValidity();
                currentX509.verify(parentX509.getPublicKey());
            } catch (Exception e) {
                return false;
            }
            currentX509 = parentX509;
            parentX509 = getIssuer(currentX509);
        }

        // check root
        try {
            currentX509.checkValidity();
            currentX509.verify(currentX509.getPublicKey());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private X509Certificate getX509CertificateFromAlias (String alias) {
        Certificate certificate =
                keyStoreRepository
                        .readCertificate(
                                KeyStoreRepository.keyStoreFilePath,
                                passwordRepository.getPassword(KeyStoreRepository.keyStoreName),
                                alias
                        );
        if (!(certificate instanceof X509Certificate x509Certificate))
            throw new RuntimeException("Unknown certificate.");

        return x509Certificate;
    }

    public static X500Name generateX500Name(X500NameDto x500NameDto) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.E, x500NameDto.getEmail());
        builder.addRDN(BCStyle.CN, x500NameDto.getCommonName());
        builder.addRDN(BCStyle.OU, x500NameDto.getOrganisationalUnit());
        builder.addRDN(BCStyle.O, x500NameDto.getOrganisation());
        builder.addRDN(BCStyle.L, x500NameDto.getLocation());
        builder.addRDN(BCStyle.ST, x500NameDto.getState());
        builder.addRDN(BCStyle.C, x500NameDto.getCountry());
        return builder.build();
    }

    private DateRange getNewCertificateDateRange(X509Certificate certificateAuthority) {
        long startTimestamp = DateConverter.convertToUnixTime(certificateAuthority.getNotBefore());
        long endTimestamp = DateConverter.convertToUnixTime(certificateAuthority.getNotAfter());
        long midTimestamp = startTimestamp + (endTimestamp - startTimestamp) / 2;

        long currentTimestamp = DateConverter.getCurrentUnixTime();

        if (currentTimestamp >= startTimestamp && currentTimestamp <= midTimestamp)
            return new DateRange(new Date(startTimestamp * 1000), new Date(midTimestamp * 1000));
        else
            return new DateRange(new Date((midTimestamp + 1) * 1000), new Date(endTimestamp * 1000));
    }

    private KeyPair generateCertificateKeyPair(String certificateAlias, CertificateType certificateType)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(4096, random);

        KeyPair keyPair = keyGen.generateKeyPair();
        if (certificateType == CertificateType.Intermediate)
            this.privateKeyRepository.writePrivateKey(keyPair.getPrivate(), certificateAlias);

        return keyPair;
    }

    private X509v3CertificateBuilder getCertificateBuilder(
            CertificateType certificateType,
            String serialNumber,
            X509Certificate certificateAuthority,
            X500Name issuerX500Name,
            X500NameDto subjectDto
    ) throws NoSuchAlgorithmException, NoSuchProviderException {
        X500Name subjectX500Name = generateX500Name(subjectDto);

        DateRange validityDateRange = getNewCertificateDateRange(certificateAuthority);

        KeyPair subjectKeys =
                generateCertificateKeyPair(
                        issuerX500Name
                                .getRDNs(BCStyle.E)[0]
                                .getFirst()
                                .getValue()
                                .toString() + "|" + serialNumber,
                        certificateType
                );

        return new JcaX509v3CertificateBuilder(
                issuerX500Name,
                new BigInteger(serialNumber, 16),
                validityDateRange.getStartDate(),
                validityDateRange.getEndDate(),
                subjectX500Name,
                subjectKeys.getPublic()
        );
    }

    private X509Certificate signAndBuildCertificate(X509v3CertificateBuilder certificateBuilder,
                                                    ContentSigner contentSigner)
            throws CertificateException {
        X509CertificateHolder certHolder = certificateBuilder.build(contentSigner);

        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider("BC");

        return certConverter.getCertificate(certHolder);
    }

    private ContentSigner getContentSigner(String issuerPrivateKeyAlias) throws OperatorCreationException {
        PrivateKey issuerPrivateKey = this.privateKeyRepository.getPrivateKey(issuerPrivateKeyAlias);

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC");
        return builder.build(issuerPrivateKey);
    }

    public CreateCertificateDto generateX509HttpsCertificate(CreateCertificateDto certificateDto)
            throws CertIOException, OperatorCreationException, CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException {
        if (!isCertValid(certificateDto.caAlias()))
            throw new RuntimeException(
                    "Certificate authority is not valid. Unable to create new certificate with it's signature."
            );

        X509Certificate certificateAuthority = getX509CertificateFromAlias(certificateDto.caAlias());

        JcaX509CertificateHolder caHolder = new JcaX509CertificateHolder(certificateAuthority);
        X500Name issuerX500Name = caHolder.getSubject();

        String serialNumber = Long.toHexString(DateConverter.getCurrentUnixTimeMillis());

        X509v3CertificateBuilder certGen =
                getCertificateBuilder(
                        certificateDto.certificateType(),
                        serialNumber,
                        certificateAuthority,
                        issuerX500Name,
                        certificateDto.subject()
                );

        switch (certificateDto.certificateType()) {
            case CertificateType.HTTPS:
                // Key Usage
                certGen.addExtension(org.bouncycastle.asn1.x509.Extension.keyUsage, true,
                        new org.bouncycastle.asn1.x509.KeyUsage(org.bouncycastle.asn1.x509.KeyUsage.digitalSignature
                                | org.bouncycastle.asn1.x509.KeyUsage.keyEncipherment));

                // Extended Key Usage extension
                certGen.addExtension(org.bouncycastle.asn1.x509.Extension.extendedKeyUsage, true,
                        new org.bouncycastle.asn1.x509.ExtendedKeyUsage(
                                new org.bouncycastle.asn1.x509.KeyPurposeId[]{
                                        org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_serverAuth
                                }
                        )
                );

                // Subject Alternative Name
                certGen.addExtension(org.bouncycastle.asn1.x509.Extension.subjectAlternativeName, false,
                        new org.bouncycastle.asn1.x509.GeneralNames(
                                new org.bouncycastle.asn1.x509.GeneralName(
                                        org.bouncycastle.asn1.x509.GeneralName.dNSName, certificateDto.domain()
                                )
                        )
                );
                break;
            case CertificateType.DigitalSigning:
                certGen.addExtension(org.bouncycastle.asn1.x509.Extension.keyUsage, true,
                        new org.bouncycastle.asn1.x509.KeyUsage(org.bouncycastle.asn1.x509.KeyUsage.digitalSignature));
                break;
            case CertificateType.Intermediate:
                // intermediate certificate
                certGen.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints, true,
                        new org.bouncycastle.asn1.x509.BasicConstraints(-1));

                // Key Usage extension for keyCertSign
                certGen.addExtension(org.bouncycastle.asn1.x509.Extension.keyUsage, true,
                        new org.bouncycastle.asn1.x509.KeyUsage(org.bouncycastle.asn1.x509.KeyUsage.keyCertSign));
                break;
        }

        X509Certificate certificate = signAndBuildCertificate(certGen, getContentSigner(certificateDto.caAlias()));
        keyStoreRepository
                .writeCertificate(
                        issuerX500Name.getRDNs(BCStyle.E)[0].getFirst().getValue().toString() + "|" + serialNumber,
                        certificate
                );
        keyStoreRepository.writeKeyStore(
                KeyStoreRepository.keyStoreFilePath,
                passwordRepository.getPassword(KeyStoreRepository.keyStoreName).toCharArray()
        );

        return new CreateCertificateDto(
                certificateDto.certificateType(),
                caHolder
                        .getIssuer()
                        .getRDNs(BCStyle.E)[0]
                        .getFirst()
                        .getValue()
                        .toString() + "|" + certificateAuthority.getSerialNumber().toString(16),
                certificateDto.subject(),
                certificateDto.domain()
        );
    }
}