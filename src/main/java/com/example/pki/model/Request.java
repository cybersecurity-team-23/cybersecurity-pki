package com.example.pki.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor
@Setter
@Getter
@SQLDelete(sql
        = "UPDATE requests "
        + "SET is_deleted = true "
        + "WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Entity
@Table(name = "requests") // Explicitly specifying the table name
public class Request {
    @Id
    @SequenceGenerator(name = "requests_seq", sequenceName = "sequence_requests", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requests_seq")
    private Long id = null;
    private String email;
    private String commonName;
    private String organisationalUnit;
    private String organisation;
    private String location;
    private String state;
    private String country;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private boolean isDeleted = false;

    public Request(String email, String commonName, String organisationalUnit, String organisation, String location,
                   String state, String country, RequestStatus status) {
        this.email = email;
        this.commonName = commonName;
        this.organisationalUnit = organisationalUnit;
        this.organisation = organisation;
        this.location = location;
        this.state = state;
        this.country = country;
        this.status = status;
    }
}