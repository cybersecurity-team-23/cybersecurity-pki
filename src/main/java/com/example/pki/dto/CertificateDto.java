package com.example.pki.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class CertificateDto {
    private String serialNumber;
    private String signatureAlgorithm;
    private X500NameDto issuer;
    private LocalDate validFrom;
    private LocalDate validTo;
    private X500NameDto subject;
    private ArrayList<String> extensions;
    private boolean isEndEntity;
    private boolean isRoot;
    private Set<CertificateDto> children = new HashSet<>();

    public CertificateDto(X509Certificate x509Certificate, X500Name issuer, X500Name subject,
                          ArrayList<String> extensions, boolean isEndEntity, boolean isRoot) {
        serialNumber = x509Certificate.getSerialNumber().toString(16);
        signatureAlgorithm = x509Certificate.getSigAlgName();
        this.issuer = new X500NameDto(issuer);
        ZoneId zoneId = ZoneId.systemDefault();
        validFrom = LocalDate.ofInstant(x509Certificate.getNotBefore().toInstant(), zoneId);
        validTo = LocalDate.ofInstant(x509Certificate.getNotAfter().toInstant(), zoneId);
        this.subject = new X500NameDto(subject);
        this.extensions = extensions;
        this.isEndEntity = isEndEntity;
        this.isRoot = isRoot;
        this.children = new HashSet<>();
    }
}
