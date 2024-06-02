package com.example.pki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CertificateDistributionDto {
    private String issuerEmail;
    private String certificateSerialNumber;
    private String base64EncodedCertificate;
    private String certificateSignature;
    private String signatureAlgorithm;
    private String signerPublicKey;
}
