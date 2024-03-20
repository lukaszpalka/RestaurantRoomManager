package com.example.restaurantroommanager.restauranttable;

import com.example.restaurantroommanager.config.TestConfig;
import com.example.restaurantroommanager.exceptions.WrongTableStateException;
import com.example.restaurantroommanager.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class OpenRestaurantTableTest {

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
    void shouldBeOccupiedAfterOpening() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 0, RestaurantTableState.FREE);
        restaurantTableRepository.save(restaurantTable);
        Integer amount = 4;

        // when
        restaurantTableService.openRestaurantTable(1L, amount);

        // then
        assertEquals(restaurantTable.getRestaurantTableState(), RestaurantTableState.OCCUPIED);
    }

    @Test
    void shouldBeAbleToBeOpenedWhenItsFree() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 0, RestaurantTableState.FREE);
        restaurantTableRepository.save(restaurantTable);
        Integer amount = 4;

        // then
        assertDoesNotThrow(() -> restaurantTableService.openRestaurantTable(1L, amount));
    }


    @Test
    void shouldNotBeAbleToBeOpenedWhenItsOccupied() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 0, RestaurantTableState.OCCUPIED);
        restaurantTableRepository.save(restaurantTable);
        Integer amount = 4;

        // then
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(1L, amount));
    }

    @Test
    void shouldNotBeAbleToBeOpenedWhenItsOccupiedWithProducts() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 0, RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
        restaurantTableRepository.save(restaurantTable);
        Integer amount = 4;

        // then
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(1L, amount));
    }

    @Test
    void shouldNotBeAbleToBeOpenedWhenItsPaid() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 0, RestaurantTableState.PAID);
        restaurantTableRepository.save(restaurantTable);
        Integer amount = 4;

        // then
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(1L, amount));
    }


    @Test
    void shouldNotBeAbleToBeOpenedWhenItsFreeButThereIsNotEnoughSpace() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 2, 0, RestaurantTableState.FREE);
        restaurantTableRepository.save(restaurantTable);
        Integer amount = 4;

        // then
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(1L, amount));
    }


}