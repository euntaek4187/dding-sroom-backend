package com.example.ddingsroom.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SwaggerConfig {

    @Bean
    @Profile("!prod")
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("My Project API 명세서")
                .version("v1")
                .description("프로젝트에 사용되는 API 문서입니다.");

        // JWT와 같이 보안 관련 설정이 필요하다면 SecurityScheme를 추가합니다.
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes("Authorization", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
