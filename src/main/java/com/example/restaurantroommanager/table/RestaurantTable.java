package com.example.restaurantroommanager.table;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "restaurant_table")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer tableCapacity;
    private Integer tableOccupancy = 0;

    @ElementCollection
    @CollectionTable(name = "product_on_table")
    private List<ProductOnTable> productsOnTable = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private RestaurantTableState restaurantTableState = RestaurantTableState.FREE;

}
