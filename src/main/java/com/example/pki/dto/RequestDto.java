package com.example.pki.dto;

import com.example.pki.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private String email;
    private String commonName;
    private String organisationalUnit;
    private String organisation;
    private String location;
    private String state;
    private String country;
    private RequestStatus status;
}
