package com.example.schoolmanager.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI schoolManagerOpenAPI() {
        // Cấu hình thông tin hiển thị trên Swagger UI
        return new OpenAPI()
                .info(new Info()
                        .title("School Manager API")
                        .description("API quản lý sinh viên")
                        .version("v1")
                        .contact(new Contact()
                                .name("School Manager")
                                .email("support@example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Swagger UI")
                        .url("/swagger-ui/index.html"));
    }
}
