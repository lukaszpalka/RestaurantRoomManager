package com.example.restaurantroommanager.product;

import com.example.restaurantroommanager.exceptions.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void addProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.name());
        product.setPrice(productDto.price());
        productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id=" + id + " not found"));
    }

    public void updateProductById(Long id, ProductDto productDto) {
        Product product = getProductById(id);
        if (productDto.price() != null) {
            product.setPrice(productDto.price());
        }
        if (productDto.name() != null) {
            product.setName(productDto.name());
        }
        productRepository.save(product);
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}
