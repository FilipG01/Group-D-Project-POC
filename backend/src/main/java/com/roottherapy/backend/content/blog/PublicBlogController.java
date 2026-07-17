package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.content.blog.dto.PublicBlogPostDetailResponse;
import com.roottherapy.backend.content.blog.dto.PublicBlogPostSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blog")
public class PublicBlogController {

    private final PublicBlogService publicBlogService;

    public PublicBlogController(
            PublicBlogService publicBlogService
    ) {
        this.publicBlogService = publicBlogService;
    }

    @GetMapping
    public Page<PublicBlogPostSummaryResponse>
    getPublishedPosts(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        return publicBlogService.getPublishedPosts(
                page,
                size
        );
    }

    @GetMapping("/{slug}")
    public PublicBlogPostDetailResponse
    getPublishedPostBySlug(
            @PathVariable String slug
    ) {
        return publicBlogService
                .getPublishedPostBySlug(slug);
    }
}