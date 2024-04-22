package com.example.pki.repository;

import com.example.pki.model.Request;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = 'APPROVED' WHERE r.id = :id")
    int approveRequest(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = 'REJECTED' WHERE r.id = :id")
    int rejectRequest(@Param("id") Long id);
}