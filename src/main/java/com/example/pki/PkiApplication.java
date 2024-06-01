package com.example.pki;

import com.example.pki.repository.KeyStoreRepository;
import com.example.pki.repository.PasswordRepository;
import com.example.pki.repository.PrivateKeyRepository;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.security.Security;

@SpringBootApplication
public class PkiApplication {
	public static void main(String[] args) {
		 // keystore folder
		 File keystoreFolder = new File(KeyStoreRepository.keyStoreDirectoryPath);
		 if (!keystoreFolder.exists() || !keystoreFolder.isDirectory()) {
			 if (keystoreFolder.mkdir())
				 System.out.println("Generating keystore folder");
         }

		 // password and key folder
		 File passwordFolder = new File(PrivateKeyRepository.privateKeysDirectoryPath);
		 if (!passwordFolder.exists() || !passwordFolder.isDirectory()) {
			 if (passwordFolder.mkdir())
				 System.out.println("Generating password folder");
		 }

		// password.csv
		File passwordFile = new File(PasswordRepository.keyStorePasswordsFilePath);
		if (!passwordFile.exists() || !passwordFile.isDirectory()) {
			try {
				if (passwordFile.createNewFile())
					System.out.println("Generating password file");
			} catch (IOException e) {
				System.out.println("The keystore passwords file could not be created.");
				return;
			}
		}

		File privateKeysAliasesFile = new File(PrivateKeyRepository.privateKeysAliasesFilePath);
		if (!privateKeysAliasesFile.exists() || !privateKeysAliasesFile.isDirectory()) {
			try {
				if (privateKeysAliasesFile.createNewFile())
					System.out.println("Generating private keys aliases file");
			} catch (IOException e) {
				System.out.println("The private keys aliases file could not be created.");
				return;
            }
        }

		SpringApplication.run(PkiApplication.class, args);
		Security.addProvider(new BouncyCastleProvider());
	}
}