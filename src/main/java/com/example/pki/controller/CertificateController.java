package com.example.pki.controller;

import com.example.pki.dto.CertificateDto;
import com.example.pki.dto.CertificateValidityDTO;
import com.example.pki.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.security.cert.CertificateEncodingException;

@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {
    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDto> getAllCertificates() {
        try {
            return new ResponseEntity<>(certificateService.getCertificateTree(), HttpStatus.OK);
        } catch (CertificateEncodingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path="/valid", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateValidityDTO> isCertificateValid(
            @RequestParam String alias
    ) {
        boolean isValid = certificateService.isCertValid(alias);
        return new ResponseEntity<>(new CertificateValidityDTO(isValid), HttpStatus.OK);
    }

    // TODO: Rebonk this

//    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> deleteCertificate() {
//        String keyStorePass = passwordRepository.getPassword(KeyStoreRepository.keyStoreName);
//        X509Certificate certificate = (X509Certificate) keyStoreRepository.readCertificate(KeyStoreRepository.keyStoreFileName, keyStorePass, certificateAliasDTO.getAlias());
//        keyStoreRepository.deleteCertificate(certificate, privateKeyRepository, certificateService);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
