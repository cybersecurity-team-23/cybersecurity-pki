package com.example.pki.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Data
public class Certificate {
    private String issuerSerialNumber;
    private String serialNumber;
    private Issuer issuer;
    private Subject subject;
    private LocalDate startDate;
    private LocalDate endDate;
    private CertificateType type;
    private CertificateStatus status;
    private String alias;
    private String signatureAlgo;
    private List<String> extensions;
}
