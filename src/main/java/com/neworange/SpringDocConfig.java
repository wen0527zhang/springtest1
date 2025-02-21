package com.neworange;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/3/20 17:55
 * @description
 */
@Configuration
public class SpringDocConfig {
    @Resource
    private SpringDocsProperties springDocsProperties;


    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                // 文档描述信息
                .info(new Info()
                        .title(springDocsProperties.getTitle())
                        .description(springDocsProperties.getDescription())
                        .version(springDocsProperties.getVersion())
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                ).externalDocs(new ExternalDocumentation()
                        .description("**** Issues Documentation")
                        .url("https://****"));
    }
                // 添加全局的header参数
//                .addSecurityItem(new SecurityRequirement()
//                        .addList(springDocsProperties.getHeader()))
//                .components(new Components()
//                        .addSecuritySchemes(springDocsProperties.getHeader(), new SecurityScheme()
//                                .name(springDocsProperties.getHeader())
//                                .scheme(springDocsProperties.getScheme())
//                                .bearerFormat("JWT")
//                                .type(SecurityScheme.Type.HTTP))



//    @Bean
//    public GroupedOpenApi fortuneApi() {
//        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
//                .pathsToMatch("/**")
//                .group("");
//        GroupedOpenApi groupedOpenApi = builder.build();
//        return groupedOpenApi;
//    }
}
