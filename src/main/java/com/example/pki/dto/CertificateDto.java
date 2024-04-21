package com.example.pki.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class CertificateDto {
    private String someData;
    private boolean isEndEntity;
    private Set<CertificateDto> children;

    public CertificateDto(String someData, boolean isEndEntity) {
        this.someData = someData;
        this.isEndEntity = isEndEntity;
        children = new HashSet<>();
    }
}
