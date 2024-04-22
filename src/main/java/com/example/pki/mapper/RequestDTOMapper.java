package com.example.pki.mapper;

import com.example.pki.dto.CreateRequestDTO;
import com.example.pki.dto.RequestDTO;
import com.example.pki.model.Request;

public class RequestDTOMapper {
    public static RequestDTO fromRequestToDTO(Request request) {
        return new RequestDTO(
                request.getId(),
                request.getCommonName(),
                request.getSurname(),
                request.getGivenName(),
                request.getOrganisation(),
                request.getOrganisationalUnit(),
                request.getCountry(),
                request.getEmail(),
                request.getUid(),
                request.getStatus()
        );
    }

    public static CreateRequestDTO fromRequestToCreateDTO(Request request) {
        return new CreateRequestDTO(
                request.getCommonName(),
                request.getSurname(),
                request.getGivenName(),
                request.getOrganisation(),
                request.getOrganisationalUnit(),
                request.getCountry(),
                request.getEmail(),
                request.getUid()
        );
    }

    public static Request fromDTOtoRequest(RequestDTO dto) {
        return new Request(
                dto.getCommonName(),
                dto.getSurname(),
                dto.getGivenName(),
                dto.getOrganisation(),
                dto.getOrganisationalUnit(),
                dto.getCountry(),
                dto.getEmail(),
                dto.getUid(),
                dto.getStatus()
        );
    }

}
