package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.content.blog.dto.*;
import com.roottherapy.backend.notification.ModerationCommunicationService;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminBlogServiceTest {

    @Mock private BlogPostRepository blogPostRepository;
    @Mock private UserRepository userRepository;
    @Mock private BlogSlugService blogSlugService;
    @Mock private ModerationCommunicationService communicationService;

    private AdminBlogService service;
    private User admin;
    private User therapist;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        service = new AdminBlogService(
                blogPostRepository, userRepository, blogSlugService, communicationService);
        adminId = UUID.randomUUID();
        admin = user(adminId, UserRole.ADMIN, "Admin", "User");
        therapist = user(UUID.randomUUID(), UserRole.THERAPIST, "Jane", "Smith");
    }

    @Test
    void publish_submittedPost_publishesAndNotifiesTherapist() {
        BlogPost post = post(BlogPostStatus.SUBMITTED, 1L);
        prepare(post);

        AdminBlogPostResponse result = service.publish(
                adminId, post.getId(), new AdminPublishBlogPostRequest(1L));

        assertEquals(BlogPostStatus.PUBLISHED, result.status());
        assertNotNull(post.getPublishedAt());
        assertSame(admin, post.getReviewedBy());
        verify(communicationService).notifyTherapistOfBlogDecision(
                therapist, post.getId(), post.getTitle(), "APPROVED", null);
    }

    @Test
    void requestChanges_submittedPost_storesFeedbackAndStatus() {
        BlogPost post = post(BlogPostStatus.SUBMITTED, 2L);
        prepare(post);

        AdminBlogPostResponse result = service.requestChanges(
                adminId, post.getId(), new AdminBlogReviewRequest("  Add more detail  ", 2L));

        assertEquals(BlogPostStatus.CHANGES_REQUESTED, result.status());
        assertEquals("Add more detail", post.getReviewNotes());
        assertSame(admin, post.getReviewedBy());
        verify(blogPostRepository).saveAndFlush(post);
    }

    @Test
    void reject_submittedPost_rejectsAndNotifiesTherapist() {
        BlogPost post = post(BlogPostStatus.SUBMITTED, 3L);
        prepare(post);

        AdminBlogPostResponse result = service.reject(
                adminId, post.getId(), new AdminBlogReviewRequest("  Not suitable  ", 3L));

        assertEquals(BlogPostStatus.REJECTED, result.status());
        assertEquals("Not suitable", result.reviewNotes());
        verify(communicationService).notifyTherapistOfBlogDecision(
                therapist, post.getId(), post.getTitle(), "REJECTED", "Not suitable");
    }

    @Test
    void requestChanges_nonSubmittedPost_throwsConflict() {
        BlogPost post = post(BlogPostStatus.DRAFT, 0L);
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(blogPostRepository.findById(post.getId())).thenReturn(Optional.of(post));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.requestChanges(
                        adminId, post.getId(), new AdminBlogReviewRequest("Revise", 0L)));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(blogPostRepository, never()).saveAndFlush(any());
    }

    @Test
    void archive_publishedPost_makesPrivateAndClearsFeaturedPublicationData() {
        BlogPost post = post(BlogPostStatus.PUBLISHED, 4L);
        post.setFeatured(true);
        post.setPublishedAt(Instant.now());
        prepare(post);

        AdminBlogPostResponse result = service.archive(
                adminId, post.getId(), new AdminBlogLifecycleRequest(4L, "Outdated"));

        assertEquals(BlogPostStatus.ARCHIVED, result.status());
        assertFalse(post.getFeatured());
        assertNull(post.getPublishedAt());
        assertNotNull(post.getArchivedAt());
    }

    @Test
    void setFeaturedAndReorder_publishedPosts_updatesPresentationFields() {
        BlogPost first = post(BlogPostStatus.PUBLISHED, 1L);
        BlogPost second = post(BlogPostStatus.PUBLISHED, 2L);
        second.setId(UUID.randomUUID());

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(blogPostRepository.findById(first.getId())).thenReturn(Optional.of(first));
        when(blogPostRepository.saveAndFlush(first)).thenReturn(first);

        service.setFeatured(adminId, first.getId(), new AdminBlogFeatureRequest(true, 1L));
        assertTrue(first.getFeatured());

        when(blogPostRepository.findAllByIdIn(anyCollection())).thenReturn(List.of(first, second));
        when(blogPostRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        AdminBlogReorderRequest request = new AdminBlogReorderRequest(List.of(
                new AdminBlogReorderRequest.ReorderItem(first.getId(), 2, 1L),
                new AdminBlogReorderRequest.ReorderItem(second.getId(), 1, 2L)
        ));

        AdminBlogReorderResponse response = service.reorderPosts(adminId, request);

        assertEquals(2, first.getDisplayOrder());
        assertEquals(1, second.getDisplayOrder());
        assertEquals(second.getId(), response.posts().getFirst().id());
        verify(blogPostRepository).flush();
    }

    private void prepare(BlogPost post) {
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(blogPostRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(blogPostRepository.saveAndFlush(post)).thenReturn(post);
    }

    private BlogPost post(BlogPostStatus status, long version) {
        BlogPost post = new BlogPost("Helpful Post", "helpful-post", therapist);
        post.setId(UUID.randomUUID());
        post.setSummary("Helpful summary");
        post.setBody("Complete and helpful article body");
        post.setKeywords(List.of("Wellbeing"));
        post.setStatus(status);
        post.setVersion(version);
        return post;
    }

    private static User user(UUID id, UserRole role, String first, String last) {
        User user = new User(first.toLowerCase() + "@example.com", "hash", first, last, role);
        user.setId(id);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return user;
    }
}
