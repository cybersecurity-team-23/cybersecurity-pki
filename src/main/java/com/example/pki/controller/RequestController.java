package com.example.pki.controller;

import com.example.pki.dto.CreateCertificateDto;
import com.example.pki.dto.CreateRequestDto;
import com.example.pki.dto.RequestDto;
import com.example.pki.mapper.RequestDtoMapper;
import com.example.pki.model.Request;
import com.example.pki.service.CertificateService;
import com.example.pki.service.RequestService;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {
        Optional<Request> request = requestService.findById(id);

        if (request.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Request requestResponse = request.get();

        return new ResponseEntity<>(RequestDtoMapper.fromRequestToDto(requestResponse), HttpStatus.OK);
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
        try {
            certificateService.generateX509HttpsCertificate(certificateDto);
            Request request = requestService.approve(id);
            return new ResponseEntity<>(RequestDtoMapper.fromRequestToDto(request), HttpStatus.OK);
        } catch (RuntimeException | CertificateException | NoSuchAlgorithmException | OperatorCreationException |
                 NoSuchProviderException | CertIOException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<RequestDto> rejectRequest(@PathVariable Long id) {
        try {
            Request request = requestService.reject(id);
            return new ResponseEntity<>(RequestDtoMapper.fromRequestToDto(request), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
