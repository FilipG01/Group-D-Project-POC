package com.roottherapy.backend.content.services;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceOfferingRepository
        extends JpaRepository<ServiceOffering, Long> {

    List<ServiceOffering> findByPublishedTrueAndArchivedFalseOrderByDisplayOrderAsc();

    List<ServiceOffering> findByArchivedFalseOrderByDisplayOrderAsc();

    List<ServiceOffering> findAllByOrderByDisplayOrderAsc();

    Optional<ServiceOffering> findBySlugAndPublishedTrueAndArchivedFalse(
            String slug
    );

    Optional<ServiceOffering> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}