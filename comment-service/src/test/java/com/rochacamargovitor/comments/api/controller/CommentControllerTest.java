
package com.rochacamargovitor.comments.api.controller;

import com.rochacamargovitor.comments.application.exception.CommentNotFoundException;
import com.rochacamargovitor.comments.application.exception.ModerationRejectedException;
import com.rochacamargovitor.comments.application.exception.ModerationServiceException;
import com.rochacamargovitor.comments.application.exception.ModerationTimeoutException;
import com.rochacamargovitor.comments.application.service.CommentService;
import com.rochacamargovitor.comments.domain.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(com.rochacamargovitor.comments.api.exception.GlobalExceptionHandler.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldCreateCommentAndReturn201() throws Exception {
        UUID commentId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-11-13T12:00:00Z");

        Comment comment = new Comment(
                commentId,
                "I love programming.",
                "John",
                createdAt
        );

        when(commentService.create(
                "I love programming.",
                "John"
        )).thenReturn(comment);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "I love programming.",
                                    "author": "John"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.text").value("I love programming."))
                .andExpect(jsonPath("$.author").value("John"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));
    }

    @Test
    void shouldReturn422WhenCommentIsRejected() throws Exception {
        when(commentService.create("I hate this.", "John"))
                .thenThrow(new ModerationRejectedException(
                        "The comment contains prohibited language."
                ));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "I hate this.",
                                    "author": "John"
                                }
                                """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.reason").value(
                        "The comment contains prohibited language."
                ));
    }

    @Test
    void shouldReturn400WhenCommentTextIsBlank() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "   ",
                                    "author": "John"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAuthorIsMissing() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "I love programming."
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn502WhenModerationServiceFails() throws Exception {
        when(commentService.create("I love programming.", "John"))
                .thenThrow(new ModerationServiceException(
                        "Moderation service communication failed.",
                        new RuntimeException("Connection refused")
                ));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "I love programming.",
                                    "author": "John"
                                }
                                """))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.reason").value(
                        "Moderation service is unavailable."
                ));
    }

    @Test
    void shouldReturn504WhenModerationServiceTimesOut() throws Exception {
        when(commentService.create("I love programming.", "John"))
                .thenThrow(new ModerationTimeoutException(
                        "Moderation service request timed out.",
                        new RuntimeException("Request timeout")
                ));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "I love programming.",
                                    "author": "John"
                                }
                                """))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.reason").value(
                        "Moderation service timed out."
                ));
    }

    @Test
    void shouldReturn200WhenCommentExists() throws Exception {
        UUID commentId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-11-13T12:00:00Z");

        Comment comment = new Comment(
                commentId,
                "I love programming.",
                "John",
                createdAt
        );

        when(commentService.getById(commentId)).thenReturn(comment);

        mockMvc.perform(get("/api/comments/" + commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.text").value("I love programming."))
                .andExpect(jsonPath("$.author").value("John"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));
    }

    @Test
    void shouldReturn404WhenCommentDoesNotExist() throws Exception {
        UUID commentId = UUID.randomUUID();

        when(commentService.getById(commentId))
                .thenThrow(new CommentNotFoundException(commentId));

        mockMvc.perform(get("/api/comments/" + commentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason").value(
                        "Comment not found. ID: " + commentId
                ));
    }

    @Test
    void shouldReturn400WhenCommentIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(get("/api/comments/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnPaginatedComments() throws Exception {
        Comment firstComment = new Comment(
                UUID.randomUUID(),
                "First comment.",
                "John",
                Instant.parse("2026-11-13T10:00:00Z")
        );

        Comment secondComment = new Comment(
                UUID.randomUUID(),
                "Second comment.",
                "Mary",
                Instant.parse("2026-11-13T11:00:00Z")
        );

        Page<Comment> commentPage = new PageImpl<>(
                List.of(firstComment, secondComment),
                PageRequest.of(0, 10),
                2
        );

        when(commentService.findAll(0, 10)).thenReturn(commentPage);

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].text").value("First comment."))
                .andExpect(jsonPath("$.content[1].text").value("Second comment."));
    }

    @Test
    void shouldUseProvidedPaginationParameters() throws Exception {
        Page<Comment> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(2, 5),
                0
        );

        when(commentService.findAll(2, 5)).thenReturn(emptyPage);

        mockMvc.perform(get("/api/comments")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content.length()").value(0));

        verify(commentService).findAll(2, 5);
    }

    @Test
    void shouldReturnBadRequestForInvalidPaginationParameters() throws Exception {
        mockMvc.perform(get("/api/comments")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/comments")
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

}
