package com.example.restaurantroommanager.restauranttable;

import com.example.restaurantroommanager.config.TestConfig;
import com.example.restaurantroommanager.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class OccupyTableWithProductsTest {
    @Mock
    private static RestaurantTableRepository restaurantTableRepository = TestConfig.restaurantTableRepository;
    @Mock
    private static ProductRepository productRepository = TestConfig.productRepository;
    private static final RestaurantTableService restaurantTableService = new RestaurantTableService(restaurantTableRepository, productRepository);

    private void setRestaurantTableParams(RestaurantTable restaurantTable, Long id, Integer capacity, Integer occupancy, RestaurantTableState restaurantTableState) {
        restaurantTable.setId(id);
        restaurantTable.setTableCapacity(capacity);
        restaurantTable.setTableOccupancy(occupancy);
        restaurantTable.setRestaurantTableState(restaurantTableState);
    }

    @Test
    void shouldBeOccupiedWithProductsAfterAddingProduct() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 0, RestaurantTableState.OCCUPIED);
        restaurantTableRepository.save(restaurantTable);
        Integer amount = 4;

        // when
        restaurantTableService.openRestaurantTable(1L, amount);

        // then
        assertEquals(restaurantTable.getRestaurantTableState(), RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
    }

}