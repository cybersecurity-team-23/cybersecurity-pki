package com.example.pki.controller;

import com.example.pki.dto.CreateRequestDTO;
import com.example.pki.dto.RequestDTO;
import com.example.pki.mapper.RequestDTOMapper;
import com.example.pki.model.Request;
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

    @Autowired
    public RequestController(RequestService service) {
        this.requestService = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RequestDTO>> getAllUnresolved() {
        Set<RequestDTO> requests = new HashSet<>();

        for (Request request : requestService.getAllUnresolved()) {
            requests.add(RequestDTOMapper.fromRequestToDTO(request));
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

        return new ResponseEntity<>(RequestDTOMapper.fromRequestToDTO(requestResponse), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateRequestDTO> createRequest(@RequestBody CreateRequestDTO dto) {
        Request request = requestService.create(
                dto.getCommonName(),
                dto.getSurname(),
                dto.getGivenName(),
                dto.getOrganisation(),
                dto.getOrganisationalUnit(),
                dto.getCountry(),
                dto.getEmail(),
                dto.getUid()
        );

        CreateRequestDTO requestDto = RequestDTOMapper.fromRequestToCreateDTO(request);

        return new ResponseEntity<>(requestDto, HttpStatus.CREATED);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        try {
            Request request = requestService.approve(id);
            return new ResponseEntity<>(RequestDTOMapper.fromRequestToDTO(request), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        try {
            Request request = requestService.reject(id);
            return new ResponseEntity<>(RequestDTOMapper.fromRequestToDTO(request), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
