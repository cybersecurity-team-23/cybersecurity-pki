package com.example.pki;

import com.example.pki.repository.KeyStoreRepository;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.security.Security;

@SpringBootApplication
public class PkiApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(PkiApplication.class, args);
		Security.addProvider(new BouncyCastleProvider());
		KeyStoreRepository keyStoreRepository = (KeyStoreRepository) context.getBean("keyStoreRepository");
	}
}