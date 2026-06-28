package com.roottherapy.backend.profile.therapist;


import com.roottherapy.backend.users.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "therapist_profiles")
public class TherapistProfile {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name= "user_id")
    private User user;

    @Column(nullable = false)
    private String qualifications;

    @Column(name = "registration_number", nullable = false, length = 100)
    private String registrationNumber;

    @Column(name = "years_experience", nullable = false)
    private Integer yearsExperience = 0;

    @Column
    private String bio;

    @Column(name = "is_accepting_clients", nullable = false)
    private Boolean acceptingClients = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    public TherapistProfile(User user, String qualifications, String registrationNumber) {
        this.user = user;
        this.qualifications = qualifications;
        this.registrationNumber = registrationNumber;
        this.yearsExperience = 0;
        this.acceptingClients = true;
    }
}
