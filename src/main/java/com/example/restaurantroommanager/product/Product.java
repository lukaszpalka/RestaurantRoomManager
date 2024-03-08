package com.example.restaurantroommanager.product;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

}
