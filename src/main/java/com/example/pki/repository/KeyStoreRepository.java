package com.example.pki.repository;

import com.example.pki.exception.HttpTransferException;
import org.springframework.http.HttpStatus;
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
    private final KeyStore keyStore;
    public static final String keyStoreDirectoryPath = "src/main/resources/keystore";
    public static final String keyStoreFilePath = keyStoreDirectoryPath + "/keystore.jks";
    public static final String keyStoreName = "keystore";

    public KeyStoreRepository() {
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException | NoSuchProviderException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Certificate store could not be accessed."
            );
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
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Certificate store could not be accessed."
            );
        }
    }

    public void writeKeyStore(String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Certificate store could not be written to."
            );
        }
    }

    public Certificate readCertificate(String keyStorePassword, String alias) {
        try {
            readKeyStore(keyStoreFilePath, keyStorePassword.toCharArray());
            if (keyStore.isCertificateEntry(alias)) {
                return keyStore.getCertificate(alias);
            }
        } catch (KeyStoreException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Certificate store could not be accessed."
            );
        }
        return null;
    }

    public void writeCertificate(String alias, X509Certificate cert) {
        try {
            keyStore.setCertificateEntry(alias, cert);
        } catch (KeyStoreException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Certificate store could not be accessed."
            );
        }
    }

    public void deleteEntry(String entryAlias) {
        try {
            keyStore.deleteEntry(entryAlias);
        } catch (KeyStoreException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Certificate store could not be accessed, or the given certificate could not be deleted."
            );
        }
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
        } catch (KeyStoreException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Certificate store could not be accessed."
            );
        }
        return certs;
    }
}