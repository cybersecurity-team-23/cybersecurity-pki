package com.example.pki.repository;

import com.example.pki.exception.HttpTransferException;
import com.example.pki.util.DateConverter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

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
        } catch (CsvValidationException | IOException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Private key store could not be accessed."
            );
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
        } catch (IOException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Private key store could not be accessed."
            );
        }
    }

    private void writePrivateKeyAlias(String alias, String name) {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(privateKeysAliasesFilePath, true));

            csvWriter.writeNext(new String[]{alias, name});

            csvWriter.close();
        } catch (IOException e) {
            throw new HttpTransferException(HttpStatus.INTERNAL_SERVER_ERROR, "Private key could not be stored.");
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
        } catch (IOException e) {
            throw new HttpTransferException(HttpStatus.INTERNAL_SERVER_ERROR, "Private key could not be stored.");
        }

        writePrivateKeyAlias(alias, privateKeyName);
    }

    private void deletePrivateKeyAlias(String alias) {
        try {
            CSVReader reader = new CSVReader(new FileReader(privateKeysAliasesFilePath));

            List<String[]> entries = new ArrayList<>();
            String[] entry;
            while (true) {
                entry = reader.readNext();
                if (entry == null) {
                    reader.close();
                    break;
                } else if (!entry[0].equals(alias)) entries.add(entry);
            }

            CSVWriter writer = new CSVWriter(new FileWriter(privateKeysAliasesFilePath));
            writer.writeAll(entries);
            writer.close();
        } catch (CsvValidationException | IOException e) {
            throw new HttpTransferException(HttpStatus.INTERNAL_SERVER_ERROR, "Private key could not be deleted.");
        }
    }

    public void deletePrivateKey(String alias) {
        String privateKeyName = getPrivateKeyNameFromAlias(alias);
        String filePath = privateKeysDirectoryPath + "/" + privateKeyName;
        File file = new File(filePath);
        if (file.exists() && file.delete()) deletePrivateKeyAlias(alias);
    }
}
