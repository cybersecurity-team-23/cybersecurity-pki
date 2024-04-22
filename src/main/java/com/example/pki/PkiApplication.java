package com.example.pki;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.security.Security;

@SpringBootApplication
public class PkiApplication {
	public static void main(String[] args) {
		SpringApplication.run(PkiApplication.class, args);
		Security.addProvider(new BouncyCastleProvider());
	}
}