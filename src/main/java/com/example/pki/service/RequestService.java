package com.example.pki.service;

import com.example.pki.model.Request;
import com.example.pki.model.RequestStatus;
import com.example.pki.repository.RequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> getAllUnresolved() {
        return requestRepository.findAllByStatus(RequestStatus.PENDING);
    }

    public Optional<Request> findById(Long id) {
        return requestRepository.findById(id);
    }

    public Request create(
            String email,
            String commonName,
            String organisationalUnit,
            String organisation,
            String location,
            String state,
            String country
    ) {

        Request request = new Request(
                email,
                commonName,
                organisationalUnit,
                organisation,
                location,
                state,
                country,
                RequestStatus.PENDING
        );

        return requestRepository.save(request);
    }

    public Request approve(Long id) {
        int updatedCount = requestRepository.approveRequest(id);
        if (updatedCount == 0) {
            throw new RuntimeException("Request not found");
        }

        Optional<Request> request = requestRepository.findById(id);

        if (request.isEmpty()) {
            throw new RuntimeException("Request not found");
        }

        return request.get();
    }

    @Transactional
    public Request reject(Long id) {
        int updatedCount = requestRepository.rejectRequest(id);
        if (updatedCount == 0) {
            throw new RuntimeException("Request not found");
        }

        Optional<Request> request = requestRepository.findById(id);

        if (request.isEmpty()) {
            throw new RuntimeException("Request not found");
        }

        return request.get();
    }
}