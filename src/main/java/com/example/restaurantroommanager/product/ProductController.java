package com.example.restaurantroommanager.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity addProduct(@RequestBody ProductDto productDto) {
        productService.addProduct(productDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateProduct(@PathVariable("id") Long id, @RequestBody ProductDto productDto) {
        productService.updateProductById(id, productDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
