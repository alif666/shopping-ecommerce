package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "grocery_items")
@Getter
@NoArgsConstructor
public class GroceryItem {
        @Id
        private String id;

        @Indexed(unique = true)
        @NotBlank
        private String name;

        @NotBlank
        @Indexed(unique = false)
        private String category;

        @PositiveOrZero
        private int quantity;

        /** Set by MongoDB auditing whenever this document is saved. */
        @LastModifiedDate
        private Instant updatedAt;


    public GroceryItem(String id, String name, String category, int quantity) {
        super();
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
    }
}
