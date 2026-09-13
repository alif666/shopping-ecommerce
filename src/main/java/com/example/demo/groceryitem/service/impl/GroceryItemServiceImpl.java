package com.example.demo.groceryitem.service.impl;

import com.example.demo.groceryitem.service.IGroceryItemService;
import com.example.demo.model.GroceryItem;
import com.example.demo.repository.GroceryItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class GroceryItemServiceImpl implements IGroceryItemService {

    private final GroceryItemRepository groceryItemRepository;

    public GroceryItemServiceImpl(GroceryItemRepository groceryItemRepository) {
        this.groceryItemRepository = groceryItemRepository;
    }

    @Override
    public Page<GroceryItem> findItems(String category, Integer minimumQuantity,
            Integer maximumQuantity, int page) {
        validateQuantityRange(minimumQuantity, maximumQuantity);

        String normalizedCategory = normalizeCategory(category);
        Pageable pageable = PageRequest.of(page, 30, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (normalizedCategory != null && minimumQuantity != null) {
            return groceryItemRepository.findByCategoryIgnoreCaseAndQuantityBetween(
                    normalizedCategory, minimumQuantity, maximumQuantity, pageable);
        }
        if (normalizedCategory != null) {
            return groceryItemRepository.findByCategoryIgnoreCase(normalizedCategory, pageable);
        }
        if (minimumQuantity != null) {
            return groceryItemRepository.findByQuantityBetween(minimumQuantity, maximumQuantity, pageable);
        }
        return groceryItemRepository.findAll(pageable);
    }

    private void validateQuantityRange(Integer minimumQuantity, Integer maximumQuantity) {
        if ((minimumQuantity == null) != (maximumQuantity == null)) {
            throw new IllegalArgumentException("Minimum and maximum quantity must be supplied together");
        }
        if (minimumQuantity != null && (minimumQuantity < 0 || maximumQuantity < 0)) {
            throw new IllegalArgumentException("Quantity bounds must be non-negative");
        }
        if (minimumQuantity != null && minimumQuantity > maximumQuantity) {
            throw new IllegalArgumentException("Minimum quantity cannot exceed maximum quantity");
        }
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        return category.trim().toLowerCase(Locale.ROOT);
    }
}
