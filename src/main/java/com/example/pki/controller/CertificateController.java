package com.example.pki.controller;

import com.example.pki.dto.*;
import com.example.pki.model.Issuer;
import com.example.pki.model.Subject;
import com.example.pki.repository.KeyStoreRepository;
import com.example.pki.repository.PasswordRepository;
import com.example.pki.repository.PrivateKeyRepository;
import com.example.pki.service.CertificateService;
import org.apache.coyote.Response;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {

    private final KeyStoreRepository keyStoreRepository;
    private final PasswordRepository passwordRepository;
    private final PrivateKeyRepository privateKeyRepository;
    private final CertificateService certificateService;

    @Autowired
    public CertificateController(KeyStoreRepository keyStoreRepository, PasswordRepository passwordRepository, PrivateKeyRepository privateKeyRepository, CertificateService certificateService) {
        this.keyStoreRepository = keyStoreRepository;
        this.passwordRepository = passwordRepository;
        this.privateKeyRepository = privateKeyRepository;
        this.certificateService = certificateService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<AllCertificatesDTO> getAllCertificates() {
        AllCertificatesDTO dto = new AllCertificatesDTO();
        String keyStorePass = passwordRepository.getPassword("test");
        keyStoreRepository.readKeyStore(keyStoreRepository.keyStoreFileName, keyStorePass.toCharArray());
        //dto.setCertificates(keyStoreRepository.getAllCertificates());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/createHTTPS")
    public ResponseEntity<CreateHTTPSCertificateDTO> createHTTPSCertificate(@RequestBody CreateHTTPSCertificateDTO createHTTPSCertificateDTO) {
        KeyPair keyPair = certificateService.generateKeyPair();
        Subject subject = certificateService.generateSubject(createHTTPSCertificateDTO.getUser(), keyPair.getPublic());

        // TODO: get the actual issuer
        Issuer placeholder = new Issuer();

        Date start = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 6);
        Date end = calendar.getTime();

        // TODO: get the actual serial number
        String serialNumber = "2";

        X509Certificate cert = certificateService.generateX509HTTPSCertificate(subject, placeholder, start, end, serialNumber, createHTTPSCertificateDTO.getDomain());

        // TODO: fill in dto
        return new ResponseEntity<>(new CreateHTTPSCertificateDTO(), HttpStatus.OK);
    }

    @PostMapping("/createSigning")
    public ResponseEntity<CreateHTTPSCertificateDTO> createSigningCertificate(@RequestBody CreateSigningCertificateDTO createSigningCertificateDTO) {
        KeyPair keyPair = certificateService.generateKeyPair();
        Subject subject = certificateService.generateSubject(createSigningCertificateDTO.getUser(), keyPair.getPublic());

        // TODO: get the actual issuer
        Issuer placeholder = new Issuer();

        Date start = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 6);
        Date end = calendar.getTime();

        // TODO: get the actual serial number
        String serialNumber = "2";

        X509Certificate cert = certificateService.generateX509SigningCertificate(subject, placeholder, start, end, serialNumber);

        // TODO: fill in dto
        return new ResponseEntity<>(new CreateHTTPSCertificateDTO(), HttpStatus.OK);

    }

    @PostMapping("/createIntermediate")
    public ResponseEntity<CreateHTTPSCertificateDTO> createIntermediateCertificate(@RequestBody CreateIntermediateCertificateDTO createIntermediateCertificateDTO) {
        KeyPair keyPair = certificateService.generateKeyPair();
        Subject subject = certificateService.generateSubject(createIntermediateCertificateDTO.getUser(), keyPair.getPublic());

        // TODO: get the actual issuer
        Issuer placeholder = new Issuer();

        Date start = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 6);
        Date end = calendar.getTime();

        // TODO: get the actual serial number
        String serialNumber = "2";

        X509Certificate cert = certificateService.generateX509SigningCertificate(subject, placeholder, start, end, serialNumber);

        // TODO: fill in dto
        return new ResponseEntity<>(new CreateHTTPSCertificateDTO(), HttpStatus.OK);
    }
}