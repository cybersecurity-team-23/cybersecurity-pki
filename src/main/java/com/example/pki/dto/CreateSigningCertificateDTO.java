package com.example.pki.dto;

import com.example.pki.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSigningCertificateDTO {
    // TODO: what else
    public String alias;
    public User user;
}
