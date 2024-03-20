package com.example.restaurantroommanager.restauranttable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductOnTable {

    private Long id;
    private String name;
    private Double price;
    private Integer amount;
}
