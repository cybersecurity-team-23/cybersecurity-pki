package com.example.pki.dto;

import com.example.pki.model.CertificateType;
import com.example.pki.model.RequestStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Long id;
    private Long issuerSerialNumber;
    private String commonName;
    private String surname;
    private String givenName;
    private String organisation;
    private String organisationalUnit;
    private String country;
    private String email;
    private CertificateType type;
    private RequestStatus status;
}
