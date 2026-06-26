package com.roottherapy.backend.profile.client;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID>{ }
