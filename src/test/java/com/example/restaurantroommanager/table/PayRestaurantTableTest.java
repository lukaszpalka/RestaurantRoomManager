package com.example.restaurantroommanager.table;

import com.example.restaurantroommanager.config.TestConfig;
import com.example.restaurantroommanager.exceptions.WrongTableStateException;
import com.example.restaurantroommanager.product.ProductRepository;
import com.example.restaurantroommanager.restauranttable.RestaurantTable;
import com.example.restaurantroommanager.restauranttable.RestaurantTableRepository;
import com.example.restaurantroommanager.restauranttable.RestaurantTableService;
import com.example.restaurantroommanager.restauranttable.RestaurantTableState;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class PayRestaurantTableTest {
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
    void shouldBePaidAfterPaying() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 4, RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
        restaurantTableRepository.save(restaurantTable);

        // when
        restaurantTableService.payRestaurantTable(1L);

        // then
        assertEquals(restaurantTable.getRestaurantTableState(), RestaurantTableState.PAID);
    }

    @Test
    void shouldBeAbleToBePaidWhenItsOccupiedWithProducts() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 4, RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
        restaurantTableRepository.save(restaurantTable);

        // then
        assertDoesNotThrow(() -> restaurantTableService.payRestaurantTable(1L));
    }

    @Test
    void shouldNotBeAbleToBePaidWhenItsFree() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 0, RestaurantTableState.FREE);
        restaurantTableRepository.save(restaurantTable);

        // then
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.payRestaurantTable(1L));
    }


    @Test
    void shouldNotBeAbleToBePaidWhenItsPaidAlready() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 0, RestaurantTableState.PAID);
        restaurantTableRepository.save(restaurantTable);

        // then
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.payRestaurantTable(1L));
    }
}