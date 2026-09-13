package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI groceryItemsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Grocery Items API")
                        .version("v1")
                        .description("Browse grocery items by category and quantity range."));
    }
}
