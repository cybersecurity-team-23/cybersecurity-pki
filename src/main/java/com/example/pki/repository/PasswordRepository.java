package com.example.pki.repository;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PasswordRepository {
    private String passwordFilepath = "src/main/resources/keyStorePasswords.csv";

    public void writePassword(String keyStoreName, String password) {
        try {
            FileWriter fw = new FileWriter(this.passwordFilepath, true);
            CSVWriter writer = new CSVWriter(fw);
            writer.writeNext(new String[]{keyStoreName, password});
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPassword(String keyStoreName) {
        try {
            FileReader fr = new FileReader(this.passwordFilepath);
            CSVReader reader = new CSVReader(fr);
            String[] entry;
            while (true) {
                entry = reader.readNext();
                if (entry == null) break;
                if (entry[0].equals(keyStoreName)) return entry[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removePassword(String keyStoreName) {
        try {
            FileReader fr = new FileReader(this.passwordFilepath);
            CSVReader reader = new CSVReader(fr);
            List<String[]> entries = new ArrayList<>();
            String[] entry;
            while (true) {
                entry = reader.readNext();
                if (entry == null) break;
                if (!entry[0].equals(keyStoreName)) entries.add(entry);
            }
            FileWriter fw = new FileWriter(this.passwordFilepath);
            CSVWriter writer = new CSVWriter(fw);
            writer.writeAll(entries);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}