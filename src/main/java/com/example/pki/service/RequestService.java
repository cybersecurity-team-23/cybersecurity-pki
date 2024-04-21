package com.example.pki.service;

import com.example.pki.model.CertificateType;
import com.example.pki.model.Request;
import com.example.pki.model.RequestStatus;
import com.example.pki.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;


@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;

    public List<Request> getAll() {
        return requestRepository.findAll();
    }

    public Optional<Request> findById(Long id) {
        return requestRepository.findById(id);
    }

    public Request create(
            Long id,
            Long issuerSerialNumber,
            String commonName,
            String surname,
            String givenName,
            String organisation,
            String organisationalUnit,
            String country,
            String email,
            CertificateType type,
            RequestStatus status
    ) {

        Request request = new Request(
            id,
            issuerSerialNumber,
            commonName,
            surname,
            givenName,
            organisation,
            organisationalUnit,
            country,
            email,
            type,
            status
        );

        return requestRepository.save(request);
    }

    // TODO: Implement this method
    public Request approve(Long id) {
        return null;
    }

    // TODO: Implement this method
    public Request reject(Long id) {
        return null;
    }
}