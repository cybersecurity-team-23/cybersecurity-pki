package com.example.pki.service;

import com.example.pki.dto.CertificateDto;
import com.example.pki.model.Issuer;
import com.example.pki.model.Subject;
import com.example.pki.model.User;
import com.example.pki.repository.KeyStoreRepository;
import com.example.pki.repository.PasswordRepository;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
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

    public CertificateService(KeyStoreRepository keyStoreRepository, PasswordRepository passwordRepository) {
        this.keyStoreRepository = keyStoreRepository;

        String[] splitKeyStorePath = KeyStoreRepository.keyStoreFileName.split("/");
        String keyStoreName = splitKeyStorePath[splitKeyStorePath.length - 1].split("\\.")[0];
        keyStoreRepository
                .readKeyStore(
                        KeyStoreRepository.keyStoreFileName,
                        passwordRepository.getPassword(keyStoreName).toCharArray()
                );
        this.passwordRepository = passwordRepository;
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
        certificate.getIssuerX500Principal().getName();
        certificate.getSerialNumber();
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

        return certificate.getBasicConstraints() == -1 || !certificate.getKeyUsage()[5];
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

        // TODO: Check if root is null

        X509Certificate rootCertificate = getRoot(randomCertificate.get());
        assert rootCertificate != null;
        return formCertificateTree(rootCertificate);
    }

    public boolean isCertValid(String alias) {
        X509Certificate currentX509 =
                (X509Certificate) keyStoreRepository.readCertificate(
                        KeyStoreRepository.keyStoreFileName,
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

    public Subject generateSubject(User user, PublicKey publicKey) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getCommonName());
        builder.addRDN(BCStyle.SURNAME, user.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, user.getGivenName());
        builder.addRDN(BCStyle.O, user.getOrganisation());
        builder.addRDN(BCStyle.OU, user.getOrganisationalUnit());
        builder.addRDN(BCStyle.C, user.getCountry());
        builder.addRDN(BCStyle.E, user.getEmail());
        builder.addRDN(BCStyle.UID, user.getId().toString());
        return new Subject(publicKey, builder.build());
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static X509Certificate generateX509Certificate(Subject subject, Issuer issuer, Date startDate, Date endDate, String serialNumber) {
        try {
            JcaContentSignerBuilder builder =
                    new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC");
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    issuer.getX500Name(),
                    new BigInteger(serialNumber),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey()
            );
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter().setProvider("BC");

            return certConverter.getCertificate(certHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static X509Certificate generateX509HTTPSCertificate(Subject subject, Issuer issuer, Date startDate, Date endDate, String serialNumber, String domain) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuer.getX500Name(),
                    new BigInteger(serialNumber),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey());

            // Subject Alternative Name
            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.subjectAlternativeName, false,
                new org.bouncycastle.asn1.x509.GeneralNames(
                        new org.bouncycastle.asn1.x509.GeneralName(org.bouncycastle.asn1.x509.GeneralName.dNSName, domain)));

            // Key Usage
            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.keyUsage, true,
                    new org.bouncycastle.asn1.x509.KeyUsage(org.bouncycastle.asn1.x509.KeyUsage.digitalSignature
                            | org.bouncycastle.asn1.x509.KeyUsage.keyEncipherment));

            // Extended Key Usage extension
            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.extendedKeyUsage, true,
                    new org.bouncycastle.asn1.x509.ExtendedKeyUsage(
                            new org.bouncycastle.asn1.x509.KeyPurposeId[]{org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_serverAuth}));

            // end entity certificate
            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints, true,
                    new org.bouncycastle.asn1.x509.BasicConstraints(0));

            X509CertificateHolder certHolder = certGen.build(contentSigner);
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");
            return certConverter.getCertificate(certHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static X509Certificate generateX509SigningCertificate(Subject subject, Issuer issuer, Date startDate, Date endDate, String serialNumber) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuer.getX500Name(),
                    new BigInteger(serialNumber),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey());

            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.keyUsage, true,
                new org.bouncycastle.asn1.x509.KeyUsage(org.bouncycastle.asn1.x509.KeyUsage.digitalSignature));

            // end entity certificate
            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints, true,
                    new org.bouncycastle.asn1.x509.BasicConstraints(0));

            X509CertificateHolder certHolder = certGen.build(contentSigner);
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");
            return certConverter.getCertificate(certHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static X509Certificate generateX509IntermediateCertificate(Subject subject, Issuer issuer, Date startDate, Date endDate, String serialNumber) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuer.getX500Name(),
                    new BigInteger(serialNumber),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey());

            // intermediate certificate
            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.basicConstraints, true,
                new org.bouncycastle.asn1.x509.BasicConstraints(-1));

            // Key Usage extension for keyCertSign
            certGen.addExtension(org.bouncycastle.asn1.x509.Extension.keyUsage, true,
                    new org.bouncycastle.asn1.x509.KeyUsage(org.bouncycastle.asn1.x509.KeyUsage.keyCertSign));

            X509CertificateHolder certHolder = certGen.build(contentSigner);
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");
            return certConverter.getCertificate(certHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}