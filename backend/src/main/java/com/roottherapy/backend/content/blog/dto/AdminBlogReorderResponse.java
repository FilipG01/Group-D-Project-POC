package com.roottherapy.backend.content.blog.dto;

import java.util.List;

public record AdminBlogReorderResponse(
        List<AdminBlogPostResponse> posts
) {
}