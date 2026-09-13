package com.example.demo.groceryitem.service;

import com.example.demo.model.GroceryItem;
import org.springframework.data.domain.Page;

public interface IGroceryItemService {

    GroceryItem findItemByName(String name);

    Page<GroceryItem> findItems(String category, Integer minimumQuantity,
            Integer maximumQuantity, int page);
}
