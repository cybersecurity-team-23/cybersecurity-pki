package com.example.pki.dto;

public record CreateCertificateDto(CertificateType certificateType, String caAlias, X500NameDto subject,
                                   String domain) {
    public CreateCertificateDto(CertificateType certificateType, String caAlias, X500NameDto subject, String domain) {
        this.certificateType = certificateType;
        this.caAlias = caAlias;
        this.subject = subject;
        this.domain = this.certificateType == CertificateType.HTTPS ? domain : null;
    }
}
