package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    CommandLineRunner createGroceryItemCollection(MongoTemplate mongoTemplate) {
        return args -> {
            String collectionName = "grocery_items";

            if (!mongoTemplate.collectionExists(collectionName)) {
                mongoTemplate.createCollection(collectionName);
            }
        };
    }
}
