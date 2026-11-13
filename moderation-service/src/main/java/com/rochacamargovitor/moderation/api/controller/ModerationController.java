package com.rochacamargovitor.moderation.api.controller;

import com.rochacamargovitor.moderation.api.dto.ModerationInput;
import com.rochacamargovitor.moderation.api.dto.ModerationOutput;
import com.rochacamargovitor.moderation.application.service.ModerationService;
import com.rochacamargovitor.moderation.domain.model.ModerationDecision;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/moderate")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping
    public ResponseEntity<ModerationOutput> moderate(
            @Valid @RequestBody ModerationInput input
            ) {
        ModerationDecision decision = moderationService.moderate(
                input.commentId(),
                input.text()
        );

        ModerationOutput output = new ModerationOutput(
                decision.approved(),
                decision.reason()
        );

        return ResponseEntity.ok(output);
    }

}
