package com.example.pki.mapper;

import com.example.pki.dto.CreateRequestDto;
import com.example.pki.dto.RequestDto;
import com.example.pki.model.Request;

public class RequestDtoMapper {
    public static RequestDto fromRequestToDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getEmail(),
                request.getCommonName(),
                request.getOrganisationalUnit(),
                request.getOrganisation(),
                request.getLocation(),
                request.getState(),
                request.getCountry(),
                request.getStatus()
        );
    }

    public static CreateRequestDto fromRequestToCreateDto(Request request) {
        return new CreateRequestDto(
                request.getEmail(),
                request.getCommonName(),
                request.getOrganisationalUnit(),
                request.getOrganisation(),
                request.getLocation(),
                request.getState(),
                request.getCountry()
        );
    }

    public static Request fromDtoToRequest(RequestDto dto) {
        return new Request(
                dto.getEmail(),
                dto.getCommonName(),
                dto.getOrganisationalUnit(),
                dto.getOrganisation(),
                dto.getLocation(),
                dto.getState(),
                dto.getCountry(),
                dto.getStatus()
        );
    }

}
