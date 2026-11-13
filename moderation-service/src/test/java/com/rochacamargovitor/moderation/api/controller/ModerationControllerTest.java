
package com.rochacamargovitor.moderation.api.controller;

import com.rochacamargovitor.moderation.application.service.ModerationService;
import com.rochacamargovitor.moderation.domain.model.ModerationDecision;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.nullValue;

@WebMvcTest(ModerationController.class)
class ModerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModerationService moderationService;

    @Test
    void shouldReturn200WhenCommentIsApproved() throws Exception {
        UUID commentId = UUID.randomUUID();

        when(moderationService.moderate(commentId, "I love programming."))
                .thenReturn(new ModerationDecision(true, null));

        mockMvc.perform(post("/api/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "commentId": "%s",
                                    "text": "I love programming."
                                }
                                """.formatted(commentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(true))
                .andExpect(jsonPath("$.reason").value(nullValue()));

        verify(moderationService).moderate(commentId, "I love programming.");
    }

    @Test
    void shouldReturn200WhenCommentIsRejected() throws Exception {
        UUID commentId = UUID.randomUUID();
        String reason = "The comment contains prohibited language.";

        when(moderationService.moderate(commentId, "I hate this."))
                .thenReturn(new ModerationDecision(false, reason));

        mockMvc.perform(post("/api/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "commentId": "%s",
                                    "text": "I hate this."
                                }
                                """.formatted(commentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(false))
                .andExpect(jsonPath("$.reason").value(reason));

        verify(moderationService).moderate(commentId, "I hate this.");
    }

    @Test
    void shouldReturn400WhenCommentIdIsInvalid() throws Exception {
        mockMvc.perform(post("/api/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "commentId": "invalid-uuid",
                                    "text": "I love programming."
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(moderationService);
    }

    @Test
    void shouldReturn400WhenCommentIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "I love programming."
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(moderationService);
    }

    @Test
    void shouldReturn400WhenCommentTextIsBlank() throws Exception {
        UUID commentId = UUID.randomUUID();

        mockMvc.perform(post("/api/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "commentId": "%s",
                                    "text": "   "
                                }
                                """.formatted(commentId)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(moderationService);
    }
}
