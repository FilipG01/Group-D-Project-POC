package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicBlogServiceTest {

    @Mock private BlogPostRepository blogPostRepository;

    @Test
    void getPublishedPosts_returnsMappedPublicPosts() {
        PublicBlogService service = new PublicBlogService(blogPostRepository);
        BlogPost post = publishedPost();
        when(blogPostRepository
                .findByStatusOrderByFeaturedDescDisplayOrderAscPublishedAtDesc(
                        eq(BlogPostStatus.PUBLISHED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(post)));

        var result = service.getPublishedPosts(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(post.getSlug(), result.getContent().getFirst().slug());
        assertEquals("Jane Smith", result.getContent().getFirst().authorName());
    }

    @Test
    void getPublishedPostBySlug_unpublishedOrMissingSlug_throwsNotFound() {
        PublicBlogService service = new PublicBlogService(blogPostRepository);
        when(blogPostRepository.findBySlugAndStatus("private-post", BlogPostStatus.PUBLISHED))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getPublishedPostBySlug(" Private-Post "));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    private static BlogPost publishedPost() {
        User author = new User("jane@example.com", "hash", "Jane", "Smith", UserRole.THERAPIST);
        author.setId(UUID.randomUUID());
        BlogPost post = new BlogPost("Public Post", "public-post", author);
        post.setId(UUID.randomUUID());
        post.setSummary("Summary");
        post.setBody("Body");
        post.setStatus(BlogPostStatus.PUBLISHED);
        post.setPublishedAt(Instant.now());
        post.setVersion(0L);
        return post;
    }
}
