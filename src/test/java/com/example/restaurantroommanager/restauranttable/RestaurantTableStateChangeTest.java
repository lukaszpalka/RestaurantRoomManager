package com.example.restaurantroommanager.restauranttable;

import com.example.restaurantroommanager.config.TestConfig;
import com.example.restaurantroommanager.exceptions.WrongTableStateException;
import com.example.restaurantroommanager.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;


class RestaurantTableStateChangeTest {

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

//    @BeforeAll
//    public static void setUp() {
//        restaurantTableRepository = Mockito.mock(RestaurantTableRepository.class);
//        Mockito.when(restaurantTableRepository.save(any(RestaurantTable.class)))
//                .thenAnswer((Answer<RestaurantTable>) invocation -> {
//                    RestaurantTable argument = invocation.getArgument(0);
//                    return restaurantTableMap.put(argument.getId(), argument);
//                });
//        Mockito.when(restaurantTableRepository.findById(anyLong()))
//                .thenAnswer((Answer<Optional<RestaurantTable>>) invocation -> {
//                    Long id = invocation.getArgument(0);
//                    return Optional.ofNullable(restaurantTableMap.get(id));
//                });
//        restaurantTableService = new RestaurantTableService(restaurantTableRepository, productRepository);
//
//        productRepository = Mockito.mock(ProductRepository.class);
//        Mockito.when(productRepository.save(any(Product.class)))
//                .thenAnswer((Answer<Product>) invocation -> {
//                    Product argument = invocation.getArgument(0);
//                    return productMap.put(argument.getId(), argument);
//                });
//        Mockito.when(productRepository.findById(anyLong()))
//                .thenAnswer((Answer<Optional<Product>>) invocation -> {
//            Long id = invocation.getArgument(0);
//            return Optional.ofNullable(productMap.get(id));
//        });
//    }

    @Test
    public void testFindById() {
        // given
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setId(1L);
        restaurantTableRepository.save(restaurantTable);

        // when
        RestaurantTable foundRestaurantTable = restaurantTableService.getRestaurantTableById(1L);

        // then
        assertNotNull(foundRestaurantTable);
    }


    @Test
    void testOpenRestaurantTable() {
        // given
        RestaurantTable restaurantTable1 = new RestaurantTable();
        setRestaurantTableParams(restaurantTable1, 1L, 6, 4, RestaurantTableState.PAID);
        RestaurantTable restaurantTable2 = new RestaurantTable();
        setRestaurantTableParams(restaurantTable2, 2L, 4, 0, RestaurantTableState.FREE);
        RestaurantTable restaurantTable3 = new RestaurantTable();
        setRestaurantTableParams(restaurantTable3, 3L, 2, 0, RestaurantTableState.FREE);
        RestaurantTable restaurantTable4 = new RestaurantTable();
        setRestaurantTableParams(restaurantTable4, 4L, 2, 2, RestaurantTableState.OCCUPIED);
        RestaurantTable restaurantTable5 = new RestaurantTable();
        setRestaurantTableParams(restaurantTable5, 5L, 6, 2, RestaurantTableState.OCCUPIED);
        RestaurantTable restaurantTable6 = new RestaurantTable();
        setRestaurantTableParams(restaurantTable6, 6L, 6, 2, RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
        RestaurantTable restaurantTable7 = new RestaurantTable();
        setRestaurantTableParams(restaurantTable7, 7L, 6, 0, RestaurantTableState.FREE);
        restaurantTableRepository.save(restaurantTable1);
        restaurantTableRepository.save(restaurantTable2);
        restaurantTableRepository.save(restaurantTable3);
        restaurantTableRepository.save(restaurantTable4);
        restaurantTableRepository.save(restaurantTable5);
        restaurantTableRepository.save(restaurantTable6);
        restaurantTableRepository.save(restaurantTable7);
        Integer amount = 4;

        // when
        restaurantTableService.openRestaurantTable(7L, amount);

        // then
        assertDoesNotThrow(() -> restaurantTableService.openRestaurantTable(2L, amount));
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(1L, amount));
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(3L, amount));
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(4L, amount));
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(5L, amount));
        assertThrows(WrongTableStateException.class, () -> restaurantTableService.openRestaurantTable(6L, amount));
        assertEquals(restaurantTable7.getRestaurantTableState(), RestaurantTableState.OCCUPIED);
    }

//    @Test
//    void testAddProductToTable() {
//        // given
//        RestaurantTable restaurantTable1 = new RestaurantTable();
//        setRestaurantTableParams(restaurantTable1, 1L, 6, 4, RestaurantTableState.PAID);
//        RestaurantTable restaurantTable2 = new RestaurantTable();
//        setRestaurantTableParams(restaurantTable2, 2L, 4, 0, RestaurantTableState.FREE);
//        RestaurantTable restaurantTable3 = new RestaurantTable();
//        setRestaurantTableParams(restaurantTable3, 3L, 2, 0, RestaurantTableState.FREE);
//        RestaurantTable restaurantTable4 = new RestaurantTable();
//        setRestaurantTableParams(restaurantTable4, 4L, 2, 2, RestaurantTableState.OCCUPIED);
//        RestaurantTable restaurantTable5 = new RestaurantTable();
//        setRestaurantTableParams(restaurantTable5, 5L, 6, 2, RestaurantTableState.OCCUPIED);
//        RestaurantTable restaurantTable6 = new RestaurantTable();
//        setRestaurantTableParams(restaurantTable6, 6L, 6, 2, RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
//        RestaurantTable restaurantTable7 = new RestaurantTable();
//        setRestaurantTableParams(restaurantTable7, 7L, 6, 0, RestaurantTableState.OCCUPIED);
//        restaurantTableMap.put(restaurantTable1.getId(), restaurantTable1);
//        restaurantTableMap.put(restaurantTable2.getId(), restaurantTable2);
//        restaurantTableMap.put(restaurantTable3.getId(), restaurantTable3);
//        restaurantTableMap.put(restaurantTable4.getId(), restaurantTable4);
//        restaurantTableMap.put(restaurantTable5.getId(), restaurantTable5);
//        restaurantTableMap.put(restaurantTable6.getId(), restaurantTable6);
//        restaurantTableMap.put(restaurantTable7.getId(), restaurantTable7);
//        ProductOnTableDto productOnTableDto = new ProductOnTableDto(1L, 2);
//        Product product =
//        productMap.put(productOnTableDto.id(), );
//
//        // when
//        restaurantTableService.addProductToTable(7L, productOnTableDto);
//
//
//        // then
//        assertDoesNotThrow(() -> restaurantTableService.addProductToTable(4L, productOnTableDto));
//        assertDoesNotThrow(() -> restaurantTableService.addProductToTable(5L, productOnTableDto));
//        assertDoesNotThrow(() -> restaurantTableService.addProductToTable(6L, productOnTableDto));
//        assertThrows(WrongTableStateException.class, () -> restaurantTableService.addProductToTable(1L, productOnTableDto));
//        assertThrows(WrongTableStateException.class, () -> restaurantTableService.addProductToTable(3L, productOnTableDto));
//        assertEquals(restaurantTable7.getRestaurantTableState(), RestaurantTableState.OCCUPIED_WITH_PRODUCTS);
//
//    }

    @Test
    void addMorePeopleToTable() {
    }

    @Test
    void payTheRestaurantTable() {


    }

    @Test
    void closeTheRestaurantTable() {
    }
}