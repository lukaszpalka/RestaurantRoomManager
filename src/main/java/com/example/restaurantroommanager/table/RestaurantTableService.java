package com.example.restaurantroommanager.table;

import com.example.restaurantroommanager.exceptions.NotPositiveNumberException;
import com.example.restaurantroommanager.exceptions.RestaurantTableNotFoundException;
import com.example.restaurantroommanager.exceptions.WrongTableStateException;
import com.example.restaurantroommanager.product.Product;
import com.example.restaurantroommanager.product.ProductRepository;
import com.example.restaurantroommanager.product.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantTableService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final ProductRepository productRepository;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository, ProductRepository productRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.productRepository = productRepository;
    }

    /* ------------------------------------------------ TABLE CRUD ------------------------------------------------ */
    public void addRestaurantTable(RestaurantTableDto restaurantTableDto) {
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setTableCapacity(restaurantTableDto.tableCapacity());
        restaurantTableRepository.save(restaurantTable);
    }

    public RestaurantTable getRestaurantTableById(Long id) throws RestaurantTableNotFoundException {
        return restaurantTableRepository.findById(id).orElseThrow(() -> new RestaurantTableNotFoundException("Restaurant table with id=" + id + " not found"));
    }

    public List<RestaurantTable> getAllRestaurantTables() {
        return restaurantTableRepository.findAll();
    }

    public void updateRestaurantTableById(Long id, RestaurantTableDto restaurantTableDto) {
        RestaurantTable restaurantTable = getRestaurantTableById(id);

        if (restaurantTableDto.tableCapacity() != null)
            restaurantTable.setTableCapacity(restaurantTableDto.tableCapacity());
        if (restaurantTableDto.tableOccupancy() != null)
            restaurantTable.setTableOccupancy(restaurantTableDto.tableOccupancy());
        if (restaurantTableDto.restaurantTableState() != null)
            restaurantTable.setRestaurantTableState(restaurantTableDto.restaurantTableState());

        restaurantTableRepository.save(restaurantTable);
    }

    public void deleteRestaurantTableById(Long id) {
        restaurantTableRepository.deleteById(id);
    }


    /* ------------------------------------------ TABLE STATE CONDITIONS ------------------------------------------ */
    private boolean isFree(RestaurantTable restaurantTable) {
        return restaurantTable.getRestaurantTableState().equals(RestaurantTableState.FREE);
    }

    private boolean isEnoughSpace(RestaurantTable restaurantTable, Integer peopleAmount) {
        return restaurantTable.getTableCapacity() - restaurantTable.getTableOccupancy() >= peopleAmount;
    }

    private boolean isOccupied(RestaurantTable restaurantTable) {
        return restaurantTable.getRestaurantTableState().equals(RestaurantTableState.OCCUPIED);
    }

    private boolean isOccupiedWithProducts(RestaurantTable restaurantTable) {
        return restaurantTable.getRestaurantTableState().equals(RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
    }

    private boolean isPaidAlready(RestaurantTable restaurantTable) {
        return restaurantTable.getRestaurantTableState().equals(RestaurantTableState.PAID);
    }

    /* ------------------------------------------- PRODUCT ON TABLE CRUD ------------------------------------------ */
    public void addProductToTable(Long id, ProductOnTableDto productOnTableDto) {
        if (productOnTableDto.amount() < 1) {
            throw new NotPositiveNumberException("Amount must be greater than 0");
        }
        RestaurantTable restaurantTable = getRestaurantTableById(id);
        Product product = new ProductService(productRepository).getProductById(productOnTableDto.id());

        if (isOccupied(restaurantTable)) {
            addNewProductOnTable(restaurantTable, productOnTableDto.amount(), product);
            restaurantTable.setRestaurantTableState(RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
            restaurantTableRepository.save(restaurantTable);
        } else if (isOccupiedWithProducts(restaurantTable)) {
            restaurantTable.getProductsOnTable().stream()
                    .filter(productOnTable -> productOnTable.getId().equals(product.getId()))
                    .findFirst()
                    .ifPresentOrElse(productOnTable -> {
                                productOnTable.setAmount(productOnTable.getAmount() + productOnTableDto.amount());
                                productOnTable.setPrice(productOnTable.getPrice() + product.getPrice() * productOnTableDto.amount());
                            },
                            () -> addNewProductOnTable(restaurantTable, productOnTableDto.amount(), product));
            restaurantTableRepository.save(restaurantTable);
        } else {
            throw new WrongTableStateException("Adding products to table with id=" + id + " not allowed");
        }
    }

    public List<ProductOnTable> getAllProductsOnTableByTableId(Long id) {
        if (isOccupiedWithProducts(getRestaurantTableById(id))) {
            return getRestaurantTableById(id).getProductsOnTable();
        } else throw new WrongTableStateException("Product list not available for table with id=" + id);
    }

    private void addNewProductOnTable(RestaurantTable restaurantTable, Integer amount, Product product) {
        restaurantTable.getProductsOnTable().add(new ProductOnTable(product.getId(), product.getName(),
                product.getPrice() * amount, amount));
    }

    private void updateProductOnTableAmount(ProductOnTable productOnTable, ProductOnTableDto productOnTableDto, Product product) {
        //jezeli amount jest dodatnie to sprawdzic roznice tableamount - amount >
        //jezeli roznica

        productOnTable.setAmount(productOnTable.getAmount() + productOnTableDto.amount());
        productOnTable.setPrice(productOnTable.getPrice() + product.getPrice() * productOnTableDto.amount());
    }

    public void updateProductOnTableByTableId(Long tableId, ProductOnTableDto productOnTableDto) {
        RestaurantTable restaurantTable = getRestaurantTableById(tableId);

        restaurantTable.getProductsOnTable().stream()
                .filter(productOnTable -> productOnTable.getId().equals(productOnTableDto.id()) && productOnTableDto.amount() != null)
                .findFirst()
                .ifPresent(productOnTable -> {
                    productOnTable.setAmount(productOnTableDto.amount());
                    restaurantTableRepository.save(restaurantTable);
                });
    }


    public void deleteProductByIdFromTableById(Long tableId, Long productId) {
        RestaurantTable restaurantTable = getRestaurantTableById(tableId);
        restaurantTable.getProductsOnTable().removeIf(productOnTable -> productOnTable.getId().equals(productId));
        restaurantTableRepository.save(restaurantTable);
    }

    /* ------------------------------------------- STATE CHANGE SERVICE ------------------------------------------- */
    public void openRestaurantTable(Long id, Integer amount) {
        RestaurantTable restaurantTable = getRestaurantTableById(id);

        if (isFree(restaurantTable) && isEnoughSpace(restaurantTable, amount)) {
            restaurantTable.setTableOccupancy(restaurantTable.getTableOccupancy() + amount);
            restaurantTable.setRestaurantTableState(RestaurantTableState.OCCUPIED);
            restaurantTableRepository.save(restaurantTable);
        } else throw new WrongTableStateException("Designated table is not free or there is not enough space at it");
    }

    public void addMorePeopleToTable(Long id, Integer amount) {
        RestaurantTable restaurantTable = getRestaurantTableById(id);
        boolean isOccupied = restaurantTable.getRestaurantTableState().equals(RestaurantTableState.OCCUPIED)
                || restaurantTable.getRestaurantTableState().equals(RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
        boolean isEnoughSpace = restaurantTable.getTableCapacity() - restaurantTable.getTableOccupancy() >= amount;
        if (isOccupied && isEnoughSpace) {
            restaurantTable.setTableOccupancy(restaurantTable.getTableOccupancy() + amount);
            restaurantTable.setRestaurantTableState(RestaurantTableState.OCCUPIED);
            restaurantTableRepository.save(restaurantTable);
        } else throw new WrongTableStateException("Designated table is not free or there is not enough space at it");
    }

    public void payTheRestaurantTable() {
        //sprawdź czy jest z produktami
        //zapłać i wywal produkty
        //zmień status na paid
    }

    public void closeTheRestaurantTable() {
        //sprawdź czy zapłacony
        //wywal ludzi ustaw occupancy na 0
        //wywal liste produktow (clear?)
        //zmień status na free
    }


}
