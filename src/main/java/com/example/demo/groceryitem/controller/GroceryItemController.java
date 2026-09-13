package com.example.demo.groceryitem.controller;

import com.example.demo.groceryitem.service.IGroceryItemService;
import com.example.demo.model.GroceryItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/grocery-items")
@Tag(name = "Grocery Items", description = "Grocery item browsing and filtering")
public class GroceryItemController {

    private final IGroceryItemService groceryItemService;

    public GroceryItemController(IGroceryItemService groceryItemService) {
        this.groceryItemService = groceryItemService;
    }

    @GetMapping("/by-name/{name}")
    @Operation(summary = "Get a grocery item by name", description = "Finds an item using its exact name.")
    public GroceryItem findItemByName(@org.springframework.web.bind.annotation.PathVariable String name) {
        return groceryItemService.findItemByName(name);
    }

    @GetMapping
    @Operation(summary = "Get grocery items", description = "Filter by category and/or an inclusive quantity range.")
    public Page<GroceryItem> findItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minimumQuantity,
            @RequestParam(required = false) Integer maximumQuantity,
            @RequestParam(defaultValue = "0") @Min(0) int page) {
        return groceryItemService.findItems(category, minimumQuantity, maximumQuantity, page);
    }
}
