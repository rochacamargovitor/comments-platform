
package com.rochacamargovitor.comments.infrastructure.client;

import com.rochacamargovitor.comments.application.exception.ModerationServiceException;
import com.rochacamargovitor.comments.application.exception.ModerationTimeoutException;
import com.rochacamargovitor.comments.infrastructure.client.dto.ModerationRequest;
import com.rochacamargovitor.comments.infrastructure.client.dto.ModerationResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.Objects;

@Component
public class ModerationClient {

    private final RestClient restClient;

    public ModerationClient(RestClient moderationRestClient) {
        this.restClient = Objects.requireNonNull(
                moderationRestClient,
                "Moderation RestClient cannot be null"
        );
    }

    public ModerationResponse moderate(ModerationRequest request) {
        try {
            return restClient.post()
                    .uri("/api/moderate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ModerationResponse.class);

        } catch (RestClientException exception) {
            if (isTimeout(exception)) {
                throw new ModerationTimeoutException(
                        "Moderation service request timed out.",
                        exception
                );
            }

            throw new ModerationServiceException(
                    "Moderation service communication failed.",
                    exception
            );
        }
    }

    private boolean isTimeout(Throwable throwable) {
        Throwable cause = throwable;

        while (cause != null) {
            if (cause instanceof HttpTimeoutException
                    || cause instanceof SocketTimeoutException) {
                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }

}
