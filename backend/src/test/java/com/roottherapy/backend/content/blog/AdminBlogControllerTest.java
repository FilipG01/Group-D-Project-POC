package com.roottherapy.backend.content.blog;

import com.roottherapy.backend.security.SecurityConfig;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBlogController.class)
@Import(SecurityConfig.class)
class AdminBlogControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private AdminBlogService adminBlogService;

    @Test
    @WithMockUser(roles = "THERAPIST")
    void publishPost_nonAdmin_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/admin/blog/{postId}/publish", UUID.randomUUID())
                        .contentType("application/json")
                        .content("{\"version\":0}"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(adminBlogService);
    }
}
