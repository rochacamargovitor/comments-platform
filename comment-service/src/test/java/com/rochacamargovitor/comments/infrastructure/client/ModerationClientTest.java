
package com.rochacamargovitor.comments.infrastructure.client;

import com.rochacamargovitor.comments.infrastructure.client.dto.ModerationRequest;
import com.rochacamargovitor.comments.infrastructure.client.dto.ModerationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class ModerationClientTest {

    private MockRestServiceServer mockServer;
    private ModerationClient moderationClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://localhost:8081");

        mockServer = MockRestServiceServer.bindTo(builder).build();

        moderationClient = new ModerationClient(builder.build());
    }

    @Test
    void shouldSendModerationRequestAndReceiveApproval() {
        UUID commentId = UUID.randomUUID();

        mockServer.expect(requestTo("http://localhost:8081/api/moderate"))
                .andExpect(method(POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "commentId": "%s",
                            "text": "I love programming."
                        }
                        """.formatted(commentId)))
                .andRespond(withSuccess("""
                        {
                            "approved": true,
                            "reason": null
                        }
                        """, MediaType.APPLICATION_JSON));

        ModerationResponse response = moderationClient.moderate(
                new ModerationRequest(commentId, "I love programming.")
        );

        assertTrue(response.approved());
        assertNull(response.reason());

        mockServer.verify();
    }

    @Test
    void shouldSendModerationRequestAndReceiveRejection() {
        UUID commentId = UUID.randomUUID();

        mockServer.expect(requestTo("http://localhost:8081/api/moderate"))
                .andExpect(method(POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("""
                        {
                            "approved": false,
                            "reason": "The comment contains prohibited language."
                        }
                        """, MediaType.APPLICATION_JSON));

        ModerationResponse response = moderationClient.moderate(
                new ModerationRequest(commentId, "I hate this.")
        );

        assertFalse(response.approved());
        assertEquals(
                "The comment contains prohibited language.",
                response.reason()
        );

        mockServer.verify();
    }

}
