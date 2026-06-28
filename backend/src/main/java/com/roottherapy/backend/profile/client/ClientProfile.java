package com.roottherapy.backend.profile.client;

import com.roottherapy.backend.users.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "client_profiles")
public class ClientProfile {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "therapy_goals_summary")
    private String therapyGoalsSummary;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_contact_method", length = 50)
    private PreferredContactMethod preferredContactMethod;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    public ClientProfile(User user){
        this.user = user;
    }

}
