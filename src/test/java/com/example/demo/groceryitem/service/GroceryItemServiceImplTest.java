package com.example.demo.groceryitem.service;

import com.example.demo.groceryitem.service.impl.GroceryItemServiceImpl;
import com.example.demo.model.GroceryItem;
import com.example.demo.repository.GroceryItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for service rules. MongoDB is intentionally not started here:
 * the repository is mocked and only the service decisions are tested.
 */
@ExtendWith(MockitoExtension.class)
class GroceryItemServiceImplTest {

    private static final Pageable DEFAULT_PAGE = PageRequest.of(
            0, 30, Sort.by(Sort.Direction.DESC, "updatedAt"));

    @Mock
    private GroceryItemRepository groceryItemRepository;

    @InjectMocks
    private GroceryItemServiceImpl groceryItemService;

    @Test
    void findItems_trimsAndNormalizesCategory_beforeCallingCategoryQuery() {
        // The repository query is IgnoreCase; normalizing here also handles
        // user input with surrounding spaces consistently.
        when(groceryItemRepository.findByCategoryIgnoreCase("grains", DEFAULT_PAGE))
                .thenReturn(Page.empty(DEFAULT_PAGE));

        groceryItemService.findItems("  GrAiNs  ", null, null, 0);

        verify(groceryItemRepository).findByCategoryIgnoreCase("grains", DEFAULT_PAGE);
    }

    @Test
    void findItems_rejectsNegativeMinimumQuantity() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> groceryItemService.findItems(null, -1, 10, 0));

        verifyNoInteractions(groceryItemRepository);
    }

    @Test
    void findItems_rejectsNegativeMaximumQuantity() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> groceryItemService.findItems(null, 0, -1, 0));

        verifyNoInteractions(groceryItemRepository);
    }

    @Test
    void findItems_rejectsQuantityRange_whenMinimumExceedsMaximum() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> groceryItemService.findItems(null, 11, 10, 0));

        verifyNoInteractions(groceryItemRepository);
    }

    @Test
    void findItems_callsFindAll_whenNoFiltersAreSupplied() {
        when(groceryItemRepository.findAll(DEFAULT_PAGE)).thenReturn(Page.empty(DEFAULT_PAGE));

        groceryItemService.findItems(null, null, null, 0);

        verify(groceryItemRepository).findAll(DEFAULT_PAGE);
    }

    @Test
    void findItems_callsCategoryQuery_whenOnlyCategoryIsSupplied() {
        when(groceryItemRepository.findByCategoryIgnoreCase("grains", DEFAULT_PAGE))
                .thenReturn(Page.empty(DEFAULT_PAGE));

        groceryItemService.findItems("grains", null, null, 0);

        verify(groceryItemRepository).findByCategoryIgnoreCase("grains", DEFAULT_PAGE);
    }

    @Test
    void findItems_callsQuantityRangeQuery_whenOnlyRangeIsSupplied() {
        when(groceryItemRepository.findByQuantityBetween(5, 10, DEFAULT_PAGE))
                .thenReturn(Page.empty(DEFAULT_PAGE));

        groceryItemService.findItems(null, 5, 10, 0);

        verify(groceryItemRepository).findByQuantityBetween(5, 10, DEFAULT_PAGE);
    }

    @Test
    void findItems_callsCombinedQuery_whenCategoryAndRangeAreSupplied() {
        when(groceryItemRepository.findByCategoryIgnoreCaseAndQuantityBetween(
                "grains", 5, 10, DEFAULT_PAGE)).thenReturn(Page.empty(DEFAULT_PAGE));

        groceryItemService.findItems("grains", 5, 10, 0);

        verify(groceryItemRepository).findByCategoryIgnoreCaseAndQuantityBetween(
                "grains", 5, 10, DEFAULT_PAGE);
    }

    @Test
    void findItems_usesUpdatedAtDescendingAsTheDefaultSort() {
        when(groceryItemRepository.findAll(org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(Page.empty());
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);

        groceryItemService.findItems(null, null, null, 0);

        verify(groceryItemRepository).findAll(pageable.capture());
        assertThat(pageable.getValue().getSort())
                .isEqualTo(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }
}
