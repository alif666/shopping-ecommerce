package com.example.demo.groceryitem.repository;

import com.example.demo.model.GroceryItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests. These exercise Spring Data's derived MongoDB
 * query, rather than mocking GroceryItemRepository.
 */
@DataMongoTest
@ActiveProfiles("test")
class GroceryItemRepositoryTest {

    @Autowired
    private GroceryItemRepository groceryItemRepository;

    @AfterEach
    void clearItems() {
        // Keep every test independent of data written by a previous test.
        groceryItemRepository.deleteAll();
    }

    @Test
    void findByName_returnsItem_whenExactNameExists() {
        // Arrange: persist the item that should be found.
        groceryItemRepository.save(rice());

        // Act: look up the item by its exact display name.
        var result = groceryItemRepository.findByName("Rice");

        // Assert: the query returns the expected document.
        assertThat(result)
                .isPresent()
                .get()
                .extracting(GroceryItem::getName, GroceryItem::getCategory, GroceryItem::getQuantity)
                .containsExactly("Rice", "Grains", 10);
    }

    @Test
    void findByName_returnsEmpty_whenNameDoesNotExist() {
        // Arrange: the database contains a different item.
        groceryItemRepository.save(rice());

        // Act and assert: an unknown name has no matching document.
        assertThat(groceryItemRepository.findByName("Milk")).isEmpty();
    }

    @Test
    void findByName_doesNotMatchDifferentLetterCase_whenSearchIsExact() {
        // This documents the current rule: findByName is case-sensitive.
        // If case-insensitive search is desired, replace this test with one
        // for findByNameIgnoreCase("rice") returning the stored Rice item.
        groceryItemRepository.save(rice());

        assertThat(groceryItemRepository.findByName("rice")).isEmpty();
    }

    @Test
    void findByCategoryAndQuantityRange_returnsOnlyMatchingItems() {
        groceryItemRepository.save(item("item-rice", "Rice", "Grains", 10));
        groceryItemRepository.save(item("item-oats", "Oats", "Grains", 5));
        groceryItemRepository.save(item("item-flour", "Flour", "Grains", 20));
        groceryItemRepository.save(item("item-apple", "Apple", "Fruit", 7));

        var result = groceryItemRepository.findByCategoryIgnoreCaseAndQuantityBetween(
                "grains", 2, 15, PageRequest.of(0, 30, Sort.by("name")));

        assertThat(result.getContent())
                .extracting(GroceryItem::getName, GroceryItem::getCategory, GroceryItem::getQuantity)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Oats", "Grains", 5),
                        org.assertj.core.groups.Tuple.tuple("Rice", "Grains", 10));
    }

    @Test
    void findByCategoryAndQuantityRange_returnsEmptyPage_whenNothingMatches() {
        groceryItemRepository.save(rice());

        var result = groceryItemRepository.findByCategoryIgnoreCaseAndQuantityBetween(
                "Dairy", 0, 30, PageRequest.of(0, 30));

        assertThat(result).isEmpty();
    }

    @Test
    void findByCategory_returnsOnlyItemsInThatCategory() {
        groceryItemRepository.save(item("item-rice", "Rice", "Grains", 10));
        groceryItemRepository.save(item("item-oats", "Oats", "Grains", 5));
        groceryItemRepository.save(item("item-apple", "Apple", "Fruit", 7));

        var result = groceryItemRepository.findByCategoryIgnoreCase(
                "GRAINS", PageRequest.of(0, 30, Sort.by("name")));

        assertThat(result.getContent())
                .extracting(GroceryItem::getName)
                .containsExactly("Oats", "Rice");
    }

    @Test
    void findByQuantityRange_returnsItemsInRangeAcrossCategories() {
        groceryItemRepository.save(item("item-rice", "Rice", "Grains", 10));
        groceryItemRepository.save(item("item-oats", "Oats", "Grains", 5));
        groceryItemRepository.save(item("item-apple", "Apple", "Fruit", 7));
        groceryItemRepository.save(item("item-flour", "Flour", "Grains", 20));

        var result = groceryItemRepository.findByQuantityBetween(
                2, 15, PageRequest.of(0, 30, Sort.by("quantity")));

        assertThat(result.getContent())
                .extracting(GroceryItem::getName, GroceryItem::getQuantity)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Oats", 5),
                        org.assertj.core.groups.Tuple.tuple("Apple", 7),
                        org.assertj.core.groups.Tuple.tuple("Rice", 10));
    }

    @Test
    void findAll_returnsAtMostThirtyItems_whenGivenThirtyItemPage() {
        for (int index = 1; index <= 31; index++) {
            groceryItemRepository.save(item("item-" + index, "Item " + index, "Test", index));
        }

        var result = groceryItemRepository.findAll(PageRequest.of(0, 30, Sort.by("name")));

        assertThat(result.getContent()).hasSize(30);
        assertThat(result.getTotalElements()).isEqualTo(31);
        assertThat(result.hasNext()).isTrue();
    }

    private GroceryItem rice() {
        // A named fixture keeps test setup concise and meaningful.
        return item("item-rice", "Rice", "Grains", 10);
    }

    private GroceryItem item(String id, String name, String category, int quantity) {
        return new GroceryItem(id, name, category, quantity);
    }

}
