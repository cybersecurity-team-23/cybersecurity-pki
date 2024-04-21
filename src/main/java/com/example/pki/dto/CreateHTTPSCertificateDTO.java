package com.example.pki.dto;

import com.example.pki.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateHTTPSCertificateDTO {
    // TODO: what else
    private String alias;
    private String domain;
    private User user;
}
