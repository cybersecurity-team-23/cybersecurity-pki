package com.example.pki.service;

import com.example.pki.exception.HttpTransferException;
import com.example.pki.model.Request;
import com.example.pki.model.RequestStatus;
import com.example.pki.repository.RequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
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
        int approvedCount = requestRepository.approveRequest(id);
        if (approvedCount == 0) {
            throw new HttpTransferException(
                    HttpStatus.NOT_FOUND,
                    "The certificate request you attempted to approve has not been found."
            );
        }

        Optional<Request> request = requestRepository.findById(id);

        if (request.isEmpty()) {
            throw new HttpTransferException(
                    HttpStatus.NOT_FOUND,
                    "The certificate request you attempted to approve has not been found."
            );
        }

        return request.get();
    }

    @Transactional
    public Request reject(Long id) {
        int rejectCount = requestRepository.rejectRequest(id);
        if (rejectCount == 0) {
            throw new HttpTransferException(
                    HttpStatus.NOT_FOUND,
                    "The certificate request you attempted to reject has not been found."
            );
        }

        Optional<Request> request = requestRepository.findById(id);

        if (request.isEmpty()) {
            throw new HttpTransferException(
                    HttpStatus.NOT_FOUND,
                    "The certificate request you attempted to reject has not been found."
            );
        }

        return request.get();
    }
}