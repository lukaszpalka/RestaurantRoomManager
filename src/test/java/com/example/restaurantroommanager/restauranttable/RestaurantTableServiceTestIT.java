package com.example.restaurantroommanager.restauranttable;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestConfiguration
@AutoConfigureWebMvc
class RestaurantTableServiceTestIT {

    @Autowired
    private RestaurantTableService restaurantTableService;

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
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        setRestaurantTableParams(restaurantTable, 1L, 6, 4, RestaurantTableState.PAID);
        restaurantTableRepository.save(restaurantTable);

        // when
        RestaurantTable restaurantTableFromDataBase = restaurantTableService.getRestaurantTableById(1L);

        // then
        assertEquals(restaurantTable, restaurantTableFromDataBase);
    }

}