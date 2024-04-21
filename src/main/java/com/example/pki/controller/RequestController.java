package com.example.pki.controller;

import com.example.pki.dto.RequestDTO;
import com.example.pki.model.CertificateType;
import com.example.pki.model.Request;
import com.example.pki.model.RequestStatus;
import com.example.pki.service.RequestService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;


@RestController
@RequestMapping(value = "/api/v1/requests")
public class RequestController {
    @Autowired
    private RequestService service;

    @GetMapping
    public ResponseEntity<?> getAll() {
        ArrayList<RequestDTO> requests = new ArrayList<>();

        for (Request request : service.getAll()) {
            requests.add(RequestDTOMapper.fromRequestToDTO(request));
        }
        
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {
        Optional<Request> request = service.findById(id);

        if (request.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Request requestResponse = request.get();

        return new ResponseEntity<>(RequestDTOMapper.fromRequestToDTO(requestResponse), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody RequestDTO dto) {
        Request request = RequestDTOMapper.fromDTOtoRequest(dto);

        service.create(
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

        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        try {
            Request request = service.approve(id);
            return new ResponseEntity<>(RequestDTOMapper.fromRequestToDTO(request), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        try {
            Request request = service.reject(id);
            return new ResponseEntity<>(RequestDTOMapper.fromRequestToDTO(request), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}