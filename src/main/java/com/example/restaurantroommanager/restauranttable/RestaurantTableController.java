package com.example.restaurantroommanager.restauranttable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/table")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    public RestaurantTableController(RestaurantTableService restaurantTableService) {
        this.restaurantTableService = restaurantTableService;
    }

    @PostMapping
    public ResponseEntity addRestaurantTable(@RequestBody RestaurantTableDto restaurantTableDto) {
        restaurantTableService.addRestaurantTable(restaurantTableDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getRestaurantTables() {
        return new ResponseEntity<>(restaurantTableService.getAllRestaurantTables(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTable> getRestaurantTableById(@PathVariable("id") Long id) {
        RestaurantTable restaurantTable = restaurantTableService.getRestaurantTableById(id);
        return new ResponseEntity<>(restaurantTable, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateRestaurantTableById(@PathVariable("id") Long id, @RequestBody RestaurantTableDto restaurantTableDto) {
        restaurantTableService.updateRestaurantTableById(id, restaurantTableDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRestaurantTableById(@PathVariable("id") Long id) {
        restaurantTableService.deleteRestaurantTableById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/{id}/product")
    public ResponseEntity addProductToTable(@PathVariable("id") Long tableId, @RequestBody ProductOnTableDto productOnTableDto) {
        restaurantTableService.addProductToTable(tableId, productOnTableDto);
        return new ResponseEntity(HttpStatus.CREATED);

    }

    @GetMapping("/{id}/product")
    public ResponseEntity<List<ProductOnTable>> getAllProductsOnTableByTableId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(restaurantTableService.getAllProductsOnTableByTableId(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/product")
    public ResponseEntity updateProductOnTableByTableId(@PathVariable("id") Long id, @RequestBody ProductOnTableDto productOnTableDto) {
        restaurantTableService.updateProductOnTableByTableId(id, productOnTableDto);
        return new ResponseEntity(HttpStatus.OK);
    }


    @DeleteMapping("/{tableId}/product/{productId}")
    public ResponseEntity deleteProductByIdFromTableById(@PathVariable("tableId") Long tableId, @PathVariable("productId") Long productId) {
        restaurantTableService.deleteProductByIdFromTableById(tableId, productId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{tableId}/product/{productId}")
    public ResponseEntity deletePartiallyProductFromTableByTableId(@PathVariable("tableId") Long tableId, @RequestBody ProductOnTableDto productOnTableDto) {
        restaurantTableService.deletePartiallyProductFromTableByTableId(tableId, productOnTableDto);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    /* --------------------- STATE CHANGE ---------------------- */

    @PatchMapping("/{tableId}/open/{amount}")
    public ResponseEntity openRestaurantTable(@PathVariable("tableId") Long tableId, @PathVariable("amount") Integer amount) {
        restaurantTableService.openRestaurantTable(tableId, amount);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/{tableId}/add/{amount}")
    public ResponseEntity addMorePeopleToRestaurantTable(@PathVariable("tableId") Long tableId, @PathVariable("amount") Integer amount) {
        restaurantTableService.addMorePeopleToTable(tableId, amount);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/{tableId}/pay")
    public ResponseEntity payTheRestaurantTable(@PathVariable("tableId") Long tableId) {
        restaurantTableService.payRestaurantTable(tableId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/{tableId}/close")
    public ResponseEntity closeTheRestaurantTable(@PathVariable("tableId") Long tableId) {
        restaurantTableService.closeTheRestaurantTable(tableId);
        return new ResponseEntity(HttpStatus.OK);
    }


}
