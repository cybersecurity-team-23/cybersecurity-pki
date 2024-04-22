package com.example.pki.repository;

import com.example.pki.model.Request;
import jakarta.transaction.Transactional;
import com.example.pki.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByStatus(RequestStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = 'APPROVED' WHERE r.id = :id")
    int approveRequest(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = 'REJECTED' WHERE r.id = :id")
    int rejectRequest(@Param("id") Long id);
}