package com.example.pki.controller;

import com.example.pki.dto.CertificateDto;
import com.example.pki.dto.CertificateValidityDTO;
import com.example.pki.dto.CreateCertificateDto;
import com.example.pki.service.CertificateService;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateCertificateDto> createCertificate(@RequestBody CreateCertificateDto certificateDto) {
        try {
            certificateService.generateX509HttpsCertificate(certificateDto);
        } catch (CertIOException | OperatorCreationException | CertificateException | NoSuchAlgorithmException |
                 NoSuchProviderException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(certificateDto, HttpStatus.CREATED);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteCertificate(@RequestParam String alias) {
        try {
            certificateService.deleteCertificate(alias);
        } catch (CertificateEncodingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
