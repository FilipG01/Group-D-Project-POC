package com.roottherapy.backend.integration;

import com.roottherapy.backend.content.blog.BlogPost;
import com.roottherapy.backend.content.blog.BlogPostRepository;
import com.roottherapy.backend.content.blog.BlogSlugService;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Transactional
class BlogSlugIntegrationTest {

    @Autowired
    private BlogSlugService blogSlugService;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void duplicateGeneratedSlug_isResolvedWithNumericSuffix() {
        User author = userRepository.saveAndFlush(new User(
                "slug-author@example.com",
                "encoded-password",
                "Slug",
                "Author",
                UserRole.THERAPIST
        ));

        String firstSlug = blogSlugService.generateUniqueSlug(
                "Managing Anxiety"
        );

        BlogPost firstPost = new BlogPost(
                "Managing Anxiety",
                firstSlug,
                author
        );
        blogPostRepository.saveAndFlush(firstPost);

        String secondSlug = blogSlugService.generateUniqueSlug(
                "Managing Anxiety"
        );

        assertEquals("managing-anxiety", firstSlug);
        assertEquals("managing-anxiety-2", secondSlug);
        assertNotEquals(firstSlug, secondSlug);
    }
}
