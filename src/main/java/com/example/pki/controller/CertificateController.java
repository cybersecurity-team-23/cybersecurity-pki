package com.example.pki.controller;

import com.example.pki.repository.KeyStoreRepository;
import com.example.pki.repository.PasswordRepository;
import com.example.pki.repository.PrivateKeyRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final KeyStoreRepository keyStoreRepository;
    private final PasswordRepository passwordRepository;
    private final PrivateKeyRepository privateKeyRepository;

    public CertificateController(KeyStoreRepository keyStoreRepository, PasswordRepository passwordRepository, PrivateKeyRepository privateKeyRepository) {
        this.keyStoreRepository = keyStoreRepository;
        this.passwordRepository = passwordRepository;
        this.privateKeyRepository = privateKeyRepository;
    }

    
}
