package com.roottherapy.backend.content.blog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogSlugServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;

    @ParameterizedTest
    @CsvSource({
            "'My First Blog Post','my-first-blog-post'",
            "'Anxiety & Stress: Helpful Tips!','anxiety-stress-helpful-tips'",
            "'Café Therapy','cafe-therapy'"
    })
    void normalizeSlug_variedTitles_returnsUrlSafeSlug(String title, String expected) {
        BlogSlugService service = new BlogSlugService(blogPostRepository);
        assertEquals(expected, service.normalizeSlug(title));
    }

    @Test
    void generateUniqueSlug_existingSlugs_addsNextAvailableSuffix() {
        BlogSlugService service = new BlogSlugService(blogPostRepository);
        when(blogPostRepository.existsBySlug("coping-with-anxiety")).thenReturn(true);
        when(blogPostRepository.existsBySlug("coping-with-anxiety-2")).thenReturn(true);
        when(blogPostRepository.existsBySlug("coping-with-anxiety-3")).thenReturn(false);

        assertEquals("coping-with-anxiety-3", service.generateUniqueSlug("Coping with Anxiety"));
    }
}
