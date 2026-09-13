package com.example.demo.groceryitem.controller;

import com.example.demo.groceryitem.service.IGroceryItemService;
import com.example.demo.model.GroceryItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-slice tests: HTTP binding, status codes, JSON, and service delegation.
 * The service is mocked; its business rules are tested in its own test class.
 */
@WebMvcTest(GroceryItemController.class)
class GroceryItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IGroceryItemService groceryItemService;

    @Test
    void findItemByName_returnsItemWhenExactNameExists() throws Exception {
        when(groceryItemService.findItemByName("Rice")).thenReturn(rice());

        mockMvc.perform(get("/api/grocery-items/by-name/{name}", "Rice"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("item-rice"))
                .andExpect(jsonPath("$.name").value("Rice"))
                .andExpect(jsonPath("$.category").value("Grains"))
                .andExpect(jsonPath("$.quantity").value(10));

        verify(groceryItemService).findItemByName("Rice");
    }

    @Test
    void findItemByName_returnsNotFoundWhenServiceCannotFindItem() throws Exception {
        when(groceryItemService.findItemByName("Milk"))
                .thenThrow(new java.util.NoSuchElementException("Grocery item not found: Milk"));

        mockMvc.perform(get("/api/grocery-items/by-name/{name}", "Milk"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Grocery item not found: Milk"));
    }

    @Test
    void findItems_returnsItemsWithDefaultFiltersAndPage() throws Exception {
        when(groceryItemService.findItems(null, null, null, 0)).thenReturn(page(0, rice()));

        mockMvc.perform(get("/api/grocery-items"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value("item-rice"))
                .andExpect(jsonPath("$.content[0].name").value("Rice"))
                .andExpect(jsonPath("$.content[0].category").value("Grains"))
                .andExpect(jsonPath("$.content[0].quantity").value(10));

        verify(groceryItemService).findItems(null, null, null, 0);
    }

    @Test
    void findItems_passesCategoryFilterToService() throws Exception {
        when(groceryItemService.findItems("grains", null, null, 0)).thenReturn(emptyPage(0));

        mockMvc.perform(get("/api/grocery-items").param("category", "grains"))
                .andExpect(status().isOk());

        verify(groceryItemService).findItems("grains", null, null, 0);
    }

    @Test
    void findItems_passesQuantityRangeToService() throws Exception {
        when(groceryItemService.findItems(null, 5, 10, 0)).thenReturn(emptyPage(0));

        mockMvc.perform(get("/api/grocery-items")
                        .param("minimumQuantity", "5")
                        .param("maximumQuantity", "10"))
                .andExpect(status().isOk());

        verify(groceryItemService).findItems(null, 5, 10, 0);
    }

    @Test
    void findItems_passesCombinedFiltersAndPageToService() throws Exception {
        when(groceryItemService.findItems("grains", 5, 10, 1)).thenReturn(emptyPage(1));

        mockMvc.perform(get("/api/grocery-items")
                        .param("category", "grains")
                        .param("minimumQuantity", "5")
                        .param("maximumQuantity", "10")
                        .param("page", "1"))
                .andExpect(status().isOk());

        verify(groceryItemService).findItems("grains", 5, 10, 1);
    }

    @Test
    void findItems_returnsOkWithEmptyContentWhenNoItemsMatch() throws Exception {
        when(groceryItemService.findItems("dairy", null, null, 0)).thenReturn(emptyPage(0));

        mockMvc.perform(get("/api/grocery-items").param("category", "dairy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void findItems_returnsBadRequestWhenQuantityIsNotAnInteger() throws Exception {
        mockMvc.perform(get("/api/grocery-items").param("minimumQuantity", "five"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findItems_returnsBadRequestWhenPageIsNotAnInteger() throws Exception {
        mockMvc.perform(get("/api/grocery-items").param("page", "first"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findItems_returnsBadRequestWhenPageIsNegative() throws Exception {
        mockMvc.perform(get("/api/grocery-items").param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findItems_returnsBadRequestWhenServiceRejectsFilters() throws Exception {
        when(groceryItemService.findItems(null, 10, 5, 0))
                .thenThrow(new IllegalArgumentException("Minimum quantity cannot exceed maximum quantity"));

        mockMvc.perform(get("/api/grocery-items")
                        .param("minimumQuantity", "10")
                        .param("maximumQuantity", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Minimum quantity cannot exceed maximum quantity"));
    }

    private Page<GroceryItem> page(int pageNumber, GroceryItem... items) {
        List<GroceryItem> content = List.of(items);
        return new PageImpl<>(content, PageRequest.of(pageNumber, 30), content.size());
    }

    private Page<GroceryItem> emptyPage(int pageNumber) {
        return page(pageNumber);
    }

    private GroceryItem rice() {
        return new GroceryItem("item-rice", "Rice", "Grains", 10);
    }
}
