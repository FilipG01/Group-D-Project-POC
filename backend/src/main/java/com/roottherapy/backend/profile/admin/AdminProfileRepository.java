package com.roottherapy.backend.profile.admin;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminProfileRepository extends JpaRepository<AdminProfile, UUID>{}