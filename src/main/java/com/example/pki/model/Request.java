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
    private String commonName;
    private String surname;
    private String givenName;
    private String organisation;
    private String organisationalUnit;
    private String country;
    private String email;
    private Long uid;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private boolean isDeleted = false;

    public Request(String commonName, String surname, String givenName, String organisation, String organisationalUnit,
                   String country, String email, Long uid, RequestStatus status) {
        this.commonName = commonName;
        this.surname = surname;
        this.givenName = givenName;
        this.organisation = organisation;
        this.organisationalUnit = organisationalUnit;
        this.country = country;
        this.email = email;
        this.uid = uid;
        this.status = status;
    }
}