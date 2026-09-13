package com.example.demo.repository;

import com.example.demo.model.GroceryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Application-wide persistence access for grocery items.
 */
public interface GroceryItemRepository extends MongoRepository<GroceryItem, String> {

    Optional<GroceryItem> findByName(String name);

    Page<GroceryItem> findByCategoryIgnoreCase(String category, Pageable pageable);

    Page<GroceryItem> findByQuantityBetween(
            int minimumQuantity, int maximumQuantity, Pageable pageable);

    Page<GroceryItem> findByCategoryIgnoreCaseAndQuantityBetween(
            String category, int minimumQuantity, int maximumQuantity, Pageable pageable);
}
