package com.example.pki.repository;

import com.example.pki.exception.HttpTransferException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;

@Repository
public class PasswordRepository {
    public static final String keyStorePasswordsFilePath =
            "src/main/resources/passwords-and-private-keys/keyStorePasswords.csv";

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
        } catch (CsvValidationException | IOException e) {
            throw new HttpTransferException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "The certificate store could not be accessed."
            );
        }

        return null;
    }
}