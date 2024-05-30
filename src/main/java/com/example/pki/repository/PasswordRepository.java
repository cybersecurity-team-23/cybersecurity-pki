package com.example.pki.repository;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PasswordRepository {
    public static final String keyStorePasswordsFilePath =
            "src/main/resources/passwords-and-private-keys/keyStorePasswords.csv";

    public void writePassword(String keyStoreName, String password) {
        try {
            FileWriter fw = new FileWriter(keyStorePasswordsFilePath, true);
            CSVWriter writer = new CSVWriter(fw);
            writer.writeNext(new String[]{keyStoreName, password});
            writer.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPassword(String keyStoreName) {
        try {
            FileReader fr = new FileReader(keyStorePasswordsFilePath);
            CSVReader reader = new CSVReader(fr);
            String[] entry;
            while (true) {
                entry = reader.readNext();
                if (entry == null) {
                    reader.close();
                    fr.close();
                    break;
                }
                if (entry[0].equals(keyStoreName)) {
                    reader.close();
                    fr.close();
                    return entry[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removePassword(String keyStoreName) {
        try {
            CSVReader reader = new CSVReader(new FileReader(keyStorePasswordsFilePath));
            List<String[]> entries = new ArrayList<>();
            String[] entry;
            while (true) {
                entry = reader.readNext();
                if (entry == null) {
                    reader.close();
                    break;
                }
                if (!entry[0].equals(keyStoreName)) entries.add(entry);
            }
            FileWriter fw = new FileWriter(keyStorePasswordsFilePath);
            CSVWriter writer = new CSVWriter(fw);
            writer.writeAll(entries);
            writer.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}