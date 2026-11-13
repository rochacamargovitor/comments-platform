package com.rochacamargovitor.moderation.application.service;

import com.rochacamargovitor.moderation.domain.model.ModerationDecision;
import com.rochacamargovitor.moderation.domain.model.ModerationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class ModerationService {

    private static final Logger logger =
            LoggerFactory.getLogger(ModerationService.class);

    private final ModerationPolicy moderationPolicy;

    public ModerationService(ModerationPolicy moderationPolicy) {
        this.moderationPolicy = Objects.requireNonNull(
                moderationPolicy,
                "Moderation policy cannot be null"
        );
    }

    public ModerationDecision moderate(UUID commentId, String text) {
        Objects.requireNonNull(commentId, "Comment ID cannot be null");

        logger.info("Starting moderation for comment ID: {}", commentId);

        ModerationDecision decision = moderationPolicy.evaluate(text);

        if (decision.approved()) {
            logger.info("Comment approved. Comment ID: {}", commentId);
        } else {
            logger.info(
                    "Comment rejected. Comment ID: {}, Reason: {}",
                    commentId,
                    decision.reason()
            );
        }

        return decision;
    }

}
