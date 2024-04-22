package com.example.pki.mapper;

import com.example.pki.dto.RequestDTO;
import com.example.pki.model.Request;

public class RequestDTOMapper {
    public static RequestDTO fromRequestToDTO(Request request) {
        return new RequestDTO(
                request.getId(),
                request.getIssuerSerialNumber(),
                request.getCommonName(),
                request.getSurname(),
                request.getGivenName(),
                request.getOrganisation(),
                request.getOrganisationalUnit(),
                request.getCountry(),
                request.getEmail(),
                request.getType(),
                request.getStatus()
        );
    }

    public static Request fromDTOtoRequest(RequestDTO dto) {
        return new Request(
                dto.getId(),
                dto.getIssuerSerialNumber(),
                dto.getCommonName(),
                dto.getSurname(),
                dto.getGivenName(),
                dto.getOrganisation(),
                dto.getOrganisationalUnit(),
                dto.getCountry(),
                dto.getEmail(),
                dto.getType(),
                dto.getStatus()
        );
    }
}
