package com.roottherapy.backend.profile.therapist.submission;

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
@Table(name = "therapist_profile_submissions")
public class TherapistProfileSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "therapist_user_id", nullable = false)
    private User therapist;

    @Column(nullable = false)
    private String qualifications;

    @Column(name = "registration_number", nullable = false, length = 100)
    private String registrationNumber;

    @Column(name = "years_experience", nullable = false)
    private Integer yearsExperience = 0;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "is_accepting_clients", nullable = false)
    private Boolean acceptingClients = true;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TherapistProfileSubmissionStatus status = TherapistProfileSubmissionStatus.DRAFT;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedBy;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public TherapistProfileSubmission(User therapist) {
        this.therapist = therapist;
    }
}
