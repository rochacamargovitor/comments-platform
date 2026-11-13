package com.rochacamargovitor.moderation.infrastructure.config;

import com.rochacamargovitor.moderation.domain.model.ModerationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModerationConfiguration {

    @Bean
    public ModerationPolicy moderationPolicy() {
        return new ModerationPolicy();
    }

}
