package com.example.restaurantroommanager.config;

import com.example.restaurantroommanager.product.ProductRepository;
import com.example.restaurantroommanager.restauranttable.RestaurantTable;
import com.example.restaurantroommanager.restauranttable.RestaurantTableRepository;
import com.example.restaurantroommanager.restauranttable.RestaurantTableService;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

public class TestConfig {
    public static RestaurantTableService restaurantTableService;
    public static RestaurantTableRepository restaurantTableRepository;
    public static ProductRepository productRepository;
    public static Map<Long, RestaurantTable> restaurantTableMap = new HashMap<>();

    static {
        restaurantTableRepository = Mockito.mock(RestaurantTableRepository.class);
        Mockito.when(restaurantTableRepository.save(any(RestaurantTable.class)))
                .thenAnswer((Answer<RestaurantTable>) invocation -> {
                    RestaurantTable argument = invocation.getArgument(0);
                    return restaurantTableMap.put(argument.getId(), argument);
                });
        Mockito.when(restaurantTableRepository.findById(anyLong()))
                .thenAnswer((Answer<Optional<RestaurantTable>>) invocation -> {
                    Long id = invocation.getArgument(0);
                    return Optional.ofNullable(restaurantTableMap.get(id));
                });
        restaurantTableService = new RestaurantTableService(restaurantTableRepository, productRepository);
    }

}
