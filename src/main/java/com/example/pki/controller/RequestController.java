package com.example.pki.controller;

import com.example.pki.dto.CreateCertificateDto;
import com.example.pki.dto.CreateRequestDto;
import com.example.pki.dto.RequestDto;
import com.example.pki.mapper.RequestDtoMapper;
import com.example.pki.model.Request;
import com.example.pki.service.CertificateService;
import com.example.pki.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/requests")
public class RequestController {
    private final RequestService requestService;
    private final CertificateService certificateService;

    @Autowired
    public RequestController(RequestService service, CertificateService certificateService) {
        this.requestService = service;
        this.certificateService = certificateService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RequestDto>> getAllUnresolved() {
        Set<RequestDto> requests = new HashSet<>();

        for (Request request : requestService.getAllUnresolved()) {
            requests.add(RequestDtoMapper.fromRequestToDto(request));
        }
        
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateRequestDto> createRequest(@RequestBody CreateRequestDto dto) {
        Request request = requestService.create(
                dto.getEmail(),
                dto.getCommonName(),
                dto.getOrganisationalUnit(),
                dto.getOrganisation(),
                dto.getLocation(),
                dto.getState(),
                dto.getCountry()
        );

        CreateRequestDto requestDto = RequestDtoMapper.fromRequestToCreateDto(request);

        return new ResponseEntity<>(requestDto, HttpStatus.CREATED);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<RequestDto> approveRequest(@PathVariable Long id,
                                                     @RequestBody CreateCertificateDto certificateDto) {
        certificateService.generateX509HttpsCertificate(certificateDto);
        return new ResponseEntity<>(RequestDtoMapper.fromRequestToDto(requestService.approve(id)), HttpStatus.OK);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<RequestDto> rejectRequest(@PathVariable Long id) {
        return new ResponseEntity<>(RequestDtoMapper.fromRequestToDto(requestService.reject(id)), HttpStatus.OK);
    }
}
