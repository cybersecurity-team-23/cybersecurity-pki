package com.example.pki.controller;

import com.example.pki.dto.AllCertificatesDTO;
import com.example.pki.model.Issuer;
import com.example.pki.model.Subject;
import com.example.pki.repository.KeyStoreRepository;
import com.example.pki.repository.PasswordRepository;
import com.example.pki.repository.PrivateKeyRepository;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final KeyStoreRepository keyStoreRepository;
    private final PasswordRepository passwordRepository;
    private final PrivateKeyRepository privateKeyRepository;

    @Autowired
    public CertificateController(KeyStoreRepository keyStoreRepository, PasswordRepository passwordRepository, PrivateKeyRepository privateKeyRepository) {
        this.keyStoreRepository = keyStoreRepository;
        this.passwordRepository = passwordRepository;
        this.privateKeyRepository = privateKeyRepository;
    }

    @GetMapping("/getAll")
    public ResponseEntity<AllCertificatesDTO> getAllCertificates() {
        AllCertificatesDTO dto = new AllCertificatesDTO();
        String keyStorePass = passwordRepository.getPassword("test");
        keyStoreRepository.readKeyStore(keyStoreRepository.keyStoreFileName, keyStorePass.toCharArray());
        //dto.setCertificates(keyStoreRepository.getAllCertificates());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
