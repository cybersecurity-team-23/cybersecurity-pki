package com.example.pki.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String commonName;
    private String surname;
    private String givenName;
    private String organisation;
    private String organisationalUnit;
    private String country;
    private String email;
}
