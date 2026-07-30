package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.content.blog.dto.CreateTherapistBlogPostRequest;
import com.roottherapy.backend.content.blog.dto.SubmitTherapistBlogPostRequest;
import com.roottherapy.backend.content.blog.dto.TherapistBlogPostResponse;
import com.roottherapy.backend.content.blog.dto.UpdateTherapistBlogPostRequest;
import com.roottherapy.backend.notification.ModerationCommunicationService;
import com.roottherapy.backend.users.AccountStatus;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TherapistBlogServiceTest {

    @Mock private BlogPostRepository blogPostRepository;
    @Mock private UserRepository userRepository;
    @Mock private BlogSlugService blogSlugService;
    @Mock private ModerationCommunicationService communicationService;

    private TherapistBlogService service;
    private User therapist;
    private UUID therapistId;

    @BeforeEach
    void setUp() {
        service = new TherapistBlogService(
                blogPostRepository,
                userRepository,
                blogSlugService,
                communicationService
        );
        therapistId = UUID.randomUUID();
        therapist = user(therapistId, UserRole.THERAPIST);
    }

    @Test
    void createDraft_validTherapistPost_savesOwnedSanitisedDraft() {
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(blogSlugService.generateUniqueSlug("Managing Anxiety")).thenReturn("managing-anxiety");
        when(blogPostRepository.save(any(BlogPost.class))).thenAnswer(invocation -> {
            BlogPost post = invocation.getArgument(0);
            post.setId(UUID.randomUUID());
            post.setVersion(0L);
            return post;
        });

        CreateTherapistBlogPostRequest request = new CreateTherapistBlogPostRequest(
                "  Managing Anxiety  ",
                "  A practical summary  ",
                "  Full article body  ",
                "  /uploads/blog/anxiety.jpg  ",
                "  Anxiety support  ",
                "  Practical coping advice  ",
                Arrays.asList(" Anxiety ", "anxiety", null, " Stress ")
        );

        TherapistBlogPostResponse result = service.createDraft(therapistId, request);

        ArgumentCaptor<BlogPost> captor = ArgumentCaptor.forClass(BlogPost.class);
        verify(blogPostRepository).save(captor.capture());
        BlogPost saved = captor.getValue();

        assertSame(therapist, saved.getAuthor());
        assertEquals("Managing Anxiety", saved.getTitle());
        assertEquals("managing-anxiety", saved.getSlug());
        assertEquals("A practical summary", saved.getSummary());
        assertEquals("Full article body", saved.getBody());
        assertEquals(List.of("Anxiety", "Stress"), saved.getKeywords());
        assertEquals(BlogPostStatus.DRAFT, result.status());
        assertFalse(saved.getFeatured());
        verifyNoInteractions(communicationService);
    }

    @Test
    void updateOwnPost_ownerTherapist_persistsChangesWithoutChangingSlug() {
        BlogPost post = post(UUID.randomUUID(), therapist, BlogPostStatus.DRAFT, 2L);
        String originalSlug = post.getSlug();
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(blogPostRepository.findByIdAndAuthorId(post.getId(), therapistId)).thenReturn(Optional.of(post));
        when(blogPostRepository.saveAndFlush(post)).thenReturn(post);

        UpdateTherapistBlogPostRequest request = new UpdateTherapistBlogPostRequest(
                "  Revised title  ", "  Revised summary  ", "  Revised body  ",
                "/uploads/blog/revised.jpg", null, null,
                Arrays.asList(" Trauma ", "trauma", " Wellbeing "), 2L
        );

        TherapistBlogPostResponse result = service.updateOwnPost(therapistId, post.getId(), request);

        assertEquals("Revised title", post.getTitle());
        assertEquals(originalSlug, post.getSlug());
        assertEquals("Revised summary", result.summary());
        assertEquals(List.of("Trauma", "Wellbeing"), result.keywords());
        verify(blogPostRepository).saveAndFlush(post);
        verifyNoInteractions(communicationService);
    }

    @Test
    void updateOwnPost_wrongTherapist_returnsNotFoundAndDoesNotSave() {
        UUID postId = UUID.randomUUID();
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(blogPostRepository.findByIdAndAuthorId(postId, therapistId)).thenReturn(Optional.empty());

        UpdateTherapistBlogPostRequest request = new UpdateTherapistBlogPostRequest(
                "Title", "Summary", "Body", null, null, null, List.of(), 0L
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.updateOwnPost(therapistId, postId, request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(blogPostRepository, never()).saveAndFlush(any());
    }

    @Test
    void submitOwnPost_completeDraft_setsSubmittedAndNotifiesAdmins() {
        BlogPost post = post(UUID.randomUUID(), therapist, BlogPostStatus.DRAFT, 1L);
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(blogPostRepository.findByIdAndAuthorId(post.getId(), therapistId)).thenReturn(Optional.of(post));
        when(blogPostRepository.saveAndFlush(post)).thenReturn(post);

        TherapistBlogPostResponse result = service.submitOwnPost(
                therapistId, post.getId(), new SubmitTherapistBlogPostRequest(1L));

        assertEquals(BlogPostStatus.SUBMITTED, result.status());
        assertNotNull(post.getSubmittedAt());
        assertNull(post.getReviewNotes());
        verify(communicationService).notifyAdminsOfBlogSubmission(
                therapist, post.getId(), post.getTitle());
    }

    @Test
    void submitOwnPost_incompleteDraft_rejectsWithoutSavingOrNotification() {
        BlogPost post = post(UUID.randomUUID(), therapist, BlogPostStatus.DRAFT, 1L);
        post.setSummary("   ");
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(blogPostRepository.findByIdAndAuthorId(post.getId(), therapistId)).thenReturn(Optional.of(post));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.submitOwnPost(
                        therapistId, post.getId(), new SubmitTherapistBlogPostRequest(1L)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals(BlogPostStatus.DRAFT, post.getStatus());
        verify(blogPostRepository, never()).saveAndFlush(any());
        verifyNoInteractions(communicationService);
    }

    private static User user(UUID id, UserRole role) {
        User user = new User("user@example.com", "hash", "Jane", "Smith", role);
        user.setId(id);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return user;
    }

    private static BlogPost post(UUID id, User author, BlogPostStatus status, long version) {
        BlogPost post = new BlogPost("Managing Anxiety", "managing-anxiety", author);
        post.setId(id);
        post.setSummary("Useful summary");
        post.setBody("Complete article body");
        post.setStatus(status);
        post.setVersion(version);
        post.setKeywords(List.of("Anxiety"));
        return post;
    }
}
