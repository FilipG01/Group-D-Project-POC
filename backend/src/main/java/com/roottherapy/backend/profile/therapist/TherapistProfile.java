package com.roottherapy.backend.profile.therapist;

import com.roottherapy.backend.users.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String qualifications;

    @Column(name = "registration_number", nullable = false, length = 100)
    private String registrationNumber;

    @Column(name = "years_experience", nullable = false)
    private Integer yearsExperience = 0;

    /*
     * Existing internal/profile biography field.
     * I'm are leaving this in place so existing functionality does not break.
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "is_accepting_clients", nullable = false)
    private Boolean acceptingClients = true;

    /*
     * Public About-page profile fields.
     */

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "public_bio", nullable = false, columnDefinition = "jsonb")
    private List<String> publicBio = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> languages = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> specialisms = new ArrayList<>();

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "is_publicly_visible", nullable = false)
    private Boolean publiclyVisible = false;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    public TherapistProfile(
            User user,
            String qualifications,
            String registrationNumber
    ) {
        this.user = user;
        this.qualifications = qualifications;
        this.registrationNumber = registrationNumber;
        this.yearsExperience = 0;
        this.acceptingClients = true;

        this.publicBio = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.specialisms = new ArrayList<>();
        this.displayOrder = 0;
        this.publiclyVisible = false;
    }
}