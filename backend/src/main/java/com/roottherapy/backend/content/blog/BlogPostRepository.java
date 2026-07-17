package com.roottherapy.backend.content.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.Collection;
import java.util.List;

public interface BlogPostRepository
        extends JpaRepository<BlogPost, UUID> {

    /*
     * Public queries
     */

    @EntityGraph(attributePaths = "author")
    Page<BlogPost>
    findByStatusOrderByFeaturedDescDisplayOrderAscPublishedAtDesc(
            BlogPostStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "author")
    Optional<BlogPost> findBySlugAndStatus(
            String slug,
            BlogPostStatus status
    );

    /*
     * Therapist ownership queries
     */

    @EntityGraph(attributePaths = "author")
    Page<BlogPost> findByAuthorIdOrderByUpdatedAtDesc(
            UUID authorId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "author")
    Optional<BlogPost> findByIdAndAuthorId(
            UUID id,
            UUID authorId
    );

    /*
     * Admin queries
     */

    @EntityGraph(attributePaths = {
            "author",
            "reviewedBy"
    })
    Page<BlogPost> findAllByOrderByUpdatedAtDesc(
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "author",
            "reviewedBy"
    })
    Page<BlogPost> findByStatusOrderBySubmittedAtAsc(
            BlogPostStatus status,
            Pageable pageable
    );

    @Override
    @EntityGraph(attributePaths = {
            "author",
            "reviewedBy"
    })
    Optional<BlogPost> findById(UUID id);

    @EntityGraph(attributePaths = {
            "author",
            "reviewedBy"
    })
    List<BlogPost> findAllByIdIn(
            Collection<UUID> ids
    );

    /*
     * Slug validation
     */

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(
            String slug,
            UUID id
    );
}