package com.example.restaurantroommanager.restauranttable;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class RestaurantTableRepositoryTest {

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    private void setRestaurantTableParams(RestaurantTable restaurantTable, Long id, Integer capacity, Integer occupancy, RestaurantTableState restaurantTableState) {
        restaurantTable.setId(id);
        restaurantTable.setTableCapacity(capacity);
        restaurantTable.setTableOccupancy(occupancy);
        restaurantTable.setRestaurantTableState(restaurantTableState);
    }

    @Test
    public void findByIdTest() {
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 4, RestaurantTableState.PAID);
        restaurantTableRepository.save(restaurantTable);

        Optional<RestaurantTable> restaurantTableFromDataBase = restaurantTableRepository.findById(1L);

        assertEquals(restaurantTable.getRestaurantTableState(), restaurantTableFromDataBase.get().getRestaurantTableState());
    }

}