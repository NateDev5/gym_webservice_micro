package com.andre_nathan.gym_webservice.member.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI memberOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym Member Service")
                        .version("1.0.0")
                        .description("APIs for managing gym members and membership plans.")
                        .contact(new Contact().name("VRMS Team").email("n/a"))
                        .license(new License().name("Apache 2.0")));
    }
}
