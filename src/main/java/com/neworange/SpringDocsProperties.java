package com.neworange;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 17:58
 * @description
 */

@Component
@ConfigurationProperties(prefix = "springdocs")
@Setter
@Getter
public class SpringDocsProperties {
    private String title;
    private String description;
    private String version;
    private String header;
    private String scheme;
}
