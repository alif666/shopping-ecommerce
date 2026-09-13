package com.example.demo.config;

import com.example.demo.model.GroceryItem;
import com.example.demo.repository.GroceryItemRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Seeds local development data only. It is deliberately excluded from the
 * test profile and remains disabled unless app.seed.enabled is true.
 */
@Configuration
@Profile("local")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class GroceryItemSeedConfig {

    private static final String[] CATEGORIES = {
            "Bakery", "Beverages", "Dairy", "Frozen", "Fruit",
            "Grains", "Meat", "Pantry", "Snacks", "Vegetables"
    };

    @Bean
    ApplicationRunner seedGroceryItems(GroceryItemRepository repository) {
        return args -> {
            for (int itemNumber = 1; itemNumber <= 100; itemNumber++) {
                String name = "Sample Grocery Item %03d".formatted(itemNumber);
                String category = CATEGORIES[(itemNumber - 1) % CATEGORIES.length];
                int quantity = (itemNumber * 7) % 101;

                // Each name is unique, so application restarts never duplicate seed data.
                if (repository.findByName(name).isEmpty()) {
                    repository.save(new GroceryItem(null, name, category, quantity));
                }
            }
        };
    }
}
