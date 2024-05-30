package com.example.pki.repository;

import com.example.pki.util.DateConverter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;

@Repository
public class PrivateKeyRepository {
    public static final String privateKeysDirectoryPath = "src/main/resources/passwords-and-private-keys";
    public static final String privateKeysAliasesFilePath = privateKeysDirectoryPath + "/privateKeysAliases.csv";

    public PrivateKeyRepository() {}

    private String getPrivateKeyNameFromAlias(String alias) {
        try {
            CSVReader csvReader = new CSVReader(new FileReader(privateKeysAliasesFilePath));

            String[] csvEntry;
            while (true) {
                csvEntry = csvReader.readNext();
                if (csvEntry == null) {
                    csvReader.close();
                    break;
                }

                if (csvEntry[0].equals(alias)) {
                    csvReader.close();
                    return csvEntry[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public PrivateKey getPrivateKey(String alias) {
        String privateKeyName = getPrivateKeyNameFromAlias(alias);
        String filePath = privateKeysDirectoryPath + "/" +  privateKeyName;

        try (FileReader fileReader = new FileReader(filePath)) {
            PEMParser pemParser = new PEMParser(fileReader);
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();

            Object object = pemParser.readObject();
            if (object instanceof PrivateKeyInfo privateKeyInfo) {
                fileReader.close();
                pemParser.close();
                return jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
            } else if (object instanceof PEMKeyPair pemKeyPair) {
                PrivateKeyInfo privateKeyInfo = pemKeyPair.getPrivateKeyInfo();
                fileReader.close();
                pemParser.close();
                return jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
            } else {
                System.err.println("Invalid PEM file format: PrivateKeyInfo expected.");
                fileReader.close();
                pemParser.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void writePrivateKeyAlias(String alias, String name) {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(privateKeysAliasesFilePath, true));

            csvWriter.writeNext(new String[] { alias, name });

            csvWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writePrivateKey(PrivateKey key, String alias) {
        String privateKeyName = DateConverter.getCurrentUnixTimeMillis() + ".pem";
        String filePath = privateKeysDirectoryPath + "/" + privateKeyName;

        try {
            FileWriter fw = new FileWriter(filePath);
            JcaPEMWriter pw = new JcaPEMWriter(fw);
            pw.writeObject(key);
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        writePrivateKeyAlias(alias, privateKeyName);
    }

    public void deletePrivateKey(String alias) {
        String privateKeyName = getPrivateKeyNameFromAlias(alias);
        String filePath = privateKeysDirectoryPath + "/" + privateKeyName;
        File file = new File(filePath);
        if (file.exists()) file.delete();
    }
}
