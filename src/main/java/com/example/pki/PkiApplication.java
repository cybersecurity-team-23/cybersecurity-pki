package com.example.pki;

import com.example.pki.repository.KeyStoreRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.security.KeyStore;

@SpringBootApplication
public class PkiApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(PkiApplication.class, args);
		KeyStoreRepository keyStoreRepository = (KeyStoreRepository) context.getBean("keyStoreRepository");
		keyStoreRepository.readKeyStore(keyStoreRepository.keyStoreFileName, "123".toCharArray());
		keyStoreRepository.writeKeyStore(keyStoreRepository.keyStoreFileName, "123".toCharArray());
	}

}
