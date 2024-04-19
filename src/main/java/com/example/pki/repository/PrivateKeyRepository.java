package com.example.pki.repository;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.PrivateKey;

@Repository
public class PrivateKeyRepository {
    private String privateKeyFolderPath = "src/main/resources/private-keys/";

    public PrivateKeyRepository() {}

    public PrivateKey getKey(String alias) {
        try {
            String filePath = this.privateKeyFolderPath + alias + ".pem";
            FileReader fr = new FileReader(filePath);
            PEMParser parser = new PEMParser(fr);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = parser.readObject();

            if (object instanceof PEMKeyPair) {
                PEMKeyPair PemKeyPair = (PEMKeyPair) object;
                KeyPair keyPair = converter.getKeyPair(PemKeyPair);
                fr.close();
                parser.close();
                return keyPair.getPrivate();
            } else {
                System.err.println("Invalid PEM file format: KeyPair expected.");
                fr.close();
                parser.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeKey(PrivateKey key, String alias) {
        try {
            String filePath = this.privateKeyFolderPath + alias + ".pem";
            FileWriter fw = new FileWriter(filePath);
            PEMWriter pw = new PEMWriter(fw);
            pw.writeObject(key);
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteKey(String alias) {
        String filePath = this.privateKeyFolderPath + alias + ".pem";
        File file = new File(filePath);
        if (file.exists()) { boolean deleted = file.delete(); }
    }
}
