package com.example.pki.controller;

import com.example.pki.dto.CertificateDistributionDto;
import com.example.pki.dto.CertificateDto;
import com.example.pki.dto.CertificateValidityDto;
import com.example.pki.dto.CreateCertificateDto;
import com.example.pki.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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
        return new ResponseEntity<>(certificateService.getCertificateTree(), HttpStatus.OK);
    }

    @GetMapping(path = "/distribute", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateDistributionDto>> distributeCertificates(
            @RequestParam(name = "recipient_email") String recipientEmail
    ) {
        return new ResponseEntity<>(certificateService.getCertificatesForRecipient(recipientEmail), HttpStatus.OK);
    }

    @GetMapping(path="/valid", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateValidityDto> isCertificateValid(
            @RequestParam String alias
    ) {
        boolean isValid = certificateService.isCertValid(alias);
        return new ResponseEntity<>(new CertificateValidityDto(isValid), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateCertificateDto> createCertificate(@RequestBody CreateCertificateDto certificateDto) {
        return new ResponseEntity<>(
                certificateService.generateX509HttpsCertificate(certificateDto),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteCertificate(@RequestParam String alias) {
        certificateService.deleteCertificate(alias);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
