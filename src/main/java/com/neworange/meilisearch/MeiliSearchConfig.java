package com.neworange.meilisearch;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/12/28 20:29
 * @ description
 */
@Configuration
public class MeiliSearchConfig {
    @Value("${meilisearch.hostUrl}")
    private String hostUrl;
    @Value("${meilisearch.apiKey}")
    private String apiKey;

    @Bean
    public Config config() {
        return new Config(hostUrl, apiKey);
    }

    @Bean
    public Client client() {
        return new Client(config());
    }
}
