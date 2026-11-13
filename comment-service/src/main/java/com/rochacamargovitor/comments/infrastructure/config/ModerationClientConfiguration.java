
package com.rochacamargovitor.comments.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ModerationClientConfiguration {

    @Bean
    public RestClient moderationRestClient(
            RestClient.Builder builder,
            @Value("${moderation.service.base-url}") String baseUrl
    ) {
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory();

        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

}
