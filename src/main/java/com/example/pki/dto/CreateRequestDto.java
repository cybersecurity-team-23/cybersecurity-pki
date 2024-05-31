package com.example.pki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateRequestDto {
    private String email;
    private String commonName;
    private String organisationalUnit;
    private String organisation;
    private String location;
    private String state;
    private String country;
}
