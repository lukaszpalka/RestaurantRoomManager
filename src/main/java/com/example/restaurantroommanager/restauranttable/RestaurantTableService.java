package com.example.restaurantroommanager.restauranttable;

import com.example.restaurantroommanager.exceptions.InvalidArgumentException;
import com.example.restaurantroommanager.exceptions.RestaurantTableNotFoundException;
import com.example.restaurantroommanager.exceptions.WrongTableStateException;
import com.example.restaurantroommanager.product.Product;
import com.example.restaurantroommanager.product.ProductRepository;
import com.example.restaurantroommanager.product.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (productOnTableDto.amount() <= 0) {
            throw new InvalidArgumentException("Amount must be greater than 0");
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
                    .ifPresentOrElse(productOnTable -> updateProductOnTableAmount(productOnTable, productOnTableDto),
                            () -> addNewProductOnTable(restaurantTable, productOnTableDto.amount(), product));
            restaurantTableRepository.save(restaurantTable);
        } else throw new WrongTableStateException("Adding products to table with id=" + id + " not allowed");
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

    private void updateProductOnTableAmount(ProductOnTable productOnTable, ProductOnTableDto productOnTableDto) {
        productOnTable.setAmount(productOnTable.getAmount() + productOnTableDto.amount());
        productOnTable.setPrice(productOnTable.getPrice() * (1 + productOnTableDto.amount()));
    }

    public void updateProductOnTableByTableId(Long tableId, ProductOnTableDto productOnTableDto) {
        RestaurantTable restaurantTable = getRestaurantTableById(tableId);

        if (isOccupiedWithProducts(restaurantTable)) {
            restaurantTable.getProductsOnTable().stream()
                    .filter(productOnTable -> productOnTable.getId().equals(productOnTableDto.id()) && productOnTableDto.amount() != null)
                    .findFirst()
                    .ifPresent(productOnTable -> {
                        productOnTable.setAmount(productOnTableDto.amount());
                        productOnTable.setPrice(productOnTable.getPrice() * (1 + productOnTableDto.amount()));
                        restaurantTableRepository.save(restaurantTable);
                    });
        } else throw new WrongTableStateException("Operation not permitted for table with id=" + tableId);
    }

    public void deleteProductByIdFromTableById(Long tableId, Long productId) {
        RestaurantTable restaurantTable = getRestaurantTableById(tableId);

        if (isOccupiedWithProducts(restaurantTable)) {
            deleteWholeProduct(restaurantTable, productId);
        } else throw new WrongTableStateException("Operation not permitted for table with id=" + tableId);
    }

    public void deletePartiallyProductFromTableByTableId(Long tableId, ProductOnTableDto productOnTableDto) {
        if (productOnTableDto.amount() <= 0) {
            throw new InvalidArgumentException("Amount must be greater than 0");
        }

        RestaurantTable restaurantTable = getRestaurantTableById(tableId);
        if (!isOccupiedWithProducts(restaurantTable)) {
            throw new WrongTableStateException("Operation not permitted for table with id=" + tableId);
        }

        restaurantTable.getProductsOnTable().stream()
                .filter(productOnTable -> productOnTable.getId().equals(productOnTableDto.id()))
                .findFirst()
                .ifPresent(productOnTable -> {
                    if (productOnTableDto.amount() >= productOnTable.getAmount()) {
                        deleteWholeProduct(restaurantTable, productOnTable.getId());
                    } else {
                        productOnTable.setAmount(productOnTable.getAmount() - productOnTableDto.amount());
                        productOnTable.setPrice(productOnTable.getPrice() * (1 - productOnTableDto.amount()));
                        restaurantTableRepository.save(restaurantTable);
                    }
                });
    }

    private void deleteWholeProduct(RestaurantTable restaurantTable, Long productId) {
        restaurantTable.getProductsOnTable().removeIf(productOnTable -> productOnTable.getId().equals(productId));
        restaurantTableRepository.save(restaurantTable);
    }

    /* ------------------------------------------- STATE CHANGE SERVICE ------------------------------------------- */
    public void openRestaurantTable(Long id, Integer amount) {
        RestaurantTable restaurantTable = getRestaurantTableById(id);

        if (isFree(restaurantTable) && isEnoughSpace(restaurantTable, amount)) {
            restaurantTable.setTableOccupancy(amount);
            restaurantTable.setRestaurantTableState(RestaurantTableState.OCCUPIED);
            restaurantTableRepository.save(restaurantTable);
        } else throw new WrongTableStateException("Designated table is not free or there is not enough space at it");
    }

    public void addMorePeopleToTable(Long id, Integer amount) {
        RestaurantTable restaurantTable = getRestaurantTableById(id);

        if ((isOccupied(restaurantTable) || isOccupiedWithProducts(restaurantTable)) && isEnoughSpace(restaurantTable, amount)) {
            restaurantTable.setTableOccupancy(restaurantTable.getTableOccupancy() + amount);
            restaurantTableRepository.save(restaurantTable);
        } else throw new WrongTableStateException("Not enough space on designated table");
    }

    public void payRestaurantTable(Long id) {
        RestaurantTable restaurantTable = getRestaurantTableById(id);

        if (!isOccupiedWithProducts(restaurantTable)) {
            throw new WrongTableStateException("Operation not permitted for table with id=" + id);
        }

        restaurantTable.setRestaurantTableState(RestaurantTableState.PAID);
        restaurantTable.getProductsOnTable().clear();
        restaurantTableRepository.save(restaurantTable);
    }

    public void closeTheRestaurantTable(Long id) {
        RestaurantTable restaurantTable = getRestaurantTableById(id);

        if (!isPaidAlready(restaurantTable)) {
            throw new WrongTableStateException("Operation not permitted for table with id=" + id);
        }

        restaurantTable.setRestaurantTableState(RestaurantTableState.FREE);
        restaurantTable.setTableOccupancy(0);
        restaurantTableRepository.save(restaurantTable);
    }


}
