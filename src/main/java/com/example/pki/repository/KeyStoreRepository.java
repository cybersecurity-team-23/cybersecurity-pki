package com.example.pki.repository;

import com.example.pki.model.Issuer;
import com.example.pki.service.CertificateService;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Repository
public class KeyStoreRepository {
    private KeyStore keyStore;
    public static final String keyStoreFileName = "src/main/resources/keystore/keystore.jks";

    public KeyStoreRepository() {
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    // if filename is null it creates a new KeyStore otherwise reads the existing
    public void readKeyStore(String fileName, char[] password) {
        try {
            if(fileName != null) {
                keyStore.load(new FileInputStream(fileName), password);
            } else {
                // create a new KeyStore
                keyStore.load(null, password);
            }
        } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }

    public void writeKeyStore(String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }

    public Issuer readIssuer(String keyStoreFile, String alias, String keyStorePassword, String keyPassword) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, keyStorePassword.toCharArray());
            Certificate cert = keyStore.getCertificate(alias);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword.toCharArray());
            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new Issuer(privateKey, cert.getPublicKey(), issuerName);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException |
                 IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Certificate readCertificate(String keyStoreFile, String keyStorePassword, String alias) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePassword.toCharArray());
            if(ks.isCertificateEntry(alias)) {
                java.security.cert.Certificate cert = ks.getCertificate(alias);
                return cert;
            }
        } catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException |
                 IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeCertificate(String alias, X509Certificate cert) {
        try {
            keyStore.setCertificateEntry(alias, cert);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void deleteCertificate(X509Certificate cert, PrivateKeyRepository privateKeyRepository, CertificateService certificateService) {
        String alias = getAliasFromCertificate(cert);
        Set<X509Certificate> children = certificateService.getCertificatesSignedBy(cert);
        try {
            keyStore.deleteEntry(alias);
            privateKeyRepository.deleteKey(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        for (X509Certificate certificate: children) deleteCertificate(certificate, privateKeyRepository, certificateService);
    }

    public String getAliasFromCertificate(X509Certificate cert) {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (cert.equals(keyStore.getCertificate(alias))) return alias;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Certificate> getAllCertificates() {
        Set<Certificate> certs = new HashSet<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = keyStore.getCertificate(alias);
                certs.add(cert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return certs;
    }

    public boolean isCertValid(String alias, String keyStorePassword, CertificateService certificateService) {
        X509Certificate currentX509 = (X509Certificate) readCertificate(keyStoreFileName, keyStorePassword, alias);;
        X509Certificate parentX509 = certificateService.getIssuer(currentX509);
        while (!certificateService.isRoot(currentX509)) {
            try {
                currentX509.checkValidity();
                currentX509.verify(parentX509.getPublicKey());
            } catch (Exception e) {
                return false;
            }
            currentX509 = parentX509;
            parentX509 = certificateService.getIssuer(currentX509);
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
}