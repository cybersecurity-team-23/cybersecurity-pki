package com.example.pki.controller;

import com.example.pki.dto.CertificateDto;
import com.example.pki.model.CertificateAliasDTO;
import com.example.pki.model.CertificateValidityDTO;
import com.example.pki.repository.KeyStoreRepository;
import com.example.pki.repository.PasswordRepository;
import com.example.pki.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {
    private final CertificateService certificateService;
    private final KeyStoreRepository keyStoreRepository;
    private final PasswordRepository passwordRepository;

    @Autowired
    public CertificateController(CertificateService certificateService, KeyStoreRepository keyStoreRepository, PasswordRepository passwordRepository) {
        this.certificateService = certificateService;
        this.keyStoreRepository = keyStoreRepository;
        this.passwordRepository = passwordRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDto> getAllCertificates() {
        return new ResponseEntity<>(certificateService.getCertificateTree(), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateValidityDTO> isCertificateValid(@RequestBody CertificateAliasDTO certificateAliasDTO) {
        String keyStorePass = passwordRepository.getPassword("keystore");
        boolean isValid = keyStoreRepository.isCertValid(certificateAliasDTO.getAlias(), keyStorePass, certificateService);
        return new ResponseEntity<>(new CertificateValidityDTO(isValid), HttpStatus.OK);
    }
}
