package com.mihaiLita.ecommerce.dao;

import com.mihaiLita.ecommerce.entity.Product;
import com.mihaiLita.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    private ProductCategory category1;
    private ProductCategory category2;

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();

        category1 = new ProductCategory();
        category1.setCategoryName("Electronics");

        category2 = new ProductCategory();
        category2.setCategoryName("Books");

        category1 = productCategoryRepository.save(category1);
        category2 = productCategoryRepository.save(category2);

        Product product1 = new Product();
        product1.setSku("LAPTOP-001");
        product1.setName("Laptop");
        product1.setDescription("High-performance laptop");
        product1.setUnitPrice(new BigDecimal("1299.99"));
        product1.setImageUrl("laptop.png");
        product1.setActive(true);
        product1.setUnitsInStock(50);
        product1.setCategory(category1);

        Product product2 = new Product();
        product2.setSku("PHONE-001");
        product2.setName("Smartphone");
        product2.setDescription("Latest smartphone model");
        product2.setUnitPrice(new BigDecimal("799.99"));
        product2.setImageUrl("smartphone.png");
        product2.setActive(true);
        product2.setUnitsInStock(100);
        product2.setCategory(category1);

        Product product3 = new Product();
        product3.setSku("BOOK-001");
        product3.setName("Java Programming Book");
        product3.setDescription("Learn Java programming");
        product3.setUnitPrice(new BigDecimal("49.99"));
        product3.setImageUrl("java-book.png");
        product3.setActive(true);
        product3.setUnitsInStock(25);
        product3.setCategory(category2);

        Product product4 = new Product();
        product4.setSku("WATCH-001");
        product4.setName("Smart Watch");
        product4.setDescription("Wearable technology");
        product4.setUnitPrice(new BigDecimal("299.99"));
        product4.setImageUrl("smartwatch.png");
        product4.setActive(true);
        product4.setUnitsInStock(75);
        product4.setCategory(category1);

        productRepository.saveAll(Arrays.asList(product1, product2, product3, product4));
    }

    @Test
    public void testFindByCategoryId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Product> electronicsProducts = productRepository.findByCategoryId(category1.getId(), pageable);
        Page<Product> bookProducts = productRepository.findByCategoryId(category2.getId(), pageable);

        // Then
        assertEquals(3, electronicsProducts.getTotalElements(), "Should find 3 electronics products");
        assertEquals(1, bookProducts.getTotalElements(), "Should find 1 book product");

        // Verify content of first page
        List<Product> electronicsList = electronicsProducts.getContent();
        assertTrue(electronicsList.stream().allMatch(p -> p.getCategory().getId().equals(category1.getId())),
                "All products should belong to the Electronics category");
    }

    @Test
    public void testFindByCategoryIdPagination() {
        // Given
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);

        // When
        Page<Product> firstPageResult = productRepository.findByCategoryId(category1.getId(), firstPage);
        Page<Product> secondPageResult = productRepository.findByCategoryId(category1.getId(), secondPage);

        // Then
        assertEquals(2, firstPageResult.getContent().size(), "First page should have 2 products");
        assertEquals(1, secondPageResult.getContent().size(), "Second page should have 1 product");
        assertEquals(3, firstPageResult.getTotalElements(), "Total elements should be 3");
        assertEquals(2, firstPageResult.getTotalPages(), "Total pages should be 2");
    }

    @Test
    public void testFindByNameContaining() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Product> smartProducts = productRepository.findByNameContaining("Smart", pageable);
        Page<Product> laptopProducts = productRepository.findByNameContaining("Laptop", pageable);
        Page<Product> bookProducts = productRepository.findByNameContaining("Book", pageable);
        Page<Product> nonExistingProducts = productRepository.findByNameContaining("Tablet", pageable);

        // Then
        assertEquals(2, smartProducts.getTotalElements(), "Should find 2 products containing 'Smart'");
        assertEquals(1, laptopProducts.getTotalElements(), "Should find 1 product containing 'Laptop'");
        assertEquals(1, bookProducts.getTotalElements(), "Should find 1 product containing 'Book'");
        assertEquals(0, nonExistingProducts.getTotalElements(), "Should find 0 products containing 'Tablet'");
    }

    @Test
    public void testFindByNameContainingCaseInsensitive() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When - case should not matter for search
        Page<Product> smartLowerCase = productRepository.findByNameContaining("smart", pageable);
        Page<Product> smartUpperCase = productRepository.findByNameContaining("SMART", pageable);

        // Then
        assertEquals(smartLowerCase.getTotalElements(), smartUpperCase.getTotalElements(),
                "Search should be case insensitive");
        assertEquals(2, smartLowerCase.getTotalElements(), "Should find 2 products regardless of case");
    }

    @Test
    public void testFindByNameContainingPartialMatch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When - should match partial names
        Page<Product> laptopProducts = productRepository.findByNameContaining("top", pageable);

        // Then
        assertEquals(1, laptopProducts.getTotalElements(), "Should find laptop with partial match 'top'");
        assertEquals("Laptop", laptopProducts.getContent().get(0).getName());
    }

    @Test
    public void testFindByNameContainingPagination() {
        // Given
        // Add more products with "Smart" in name to test pagination
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setSku("SMART-00" + i);
            product.setName("Smart Device " + i);
            product.setDescription("Additional smart device " + i);
            product.setUnitPrice(new BigDecimal("99.99"));
            product.setImageUrl("smart-device-" + i + ".png");
            product.setActive(true);
            product.setUnitsInStock(30);
            product.setCategory(category1);
            productRepository.save(product);
        }

        Pageable firstPage = PageRequest.of(0, 3);
        Pageable secondPage = PageRequest.of(1, 3);

        // When
        Page<Product> firstPageResult = productRepository.findByNameContaining("Smart", firstPage);
        Page<Product> secondPageResult = productRepository.findByNameContaining("Smart", secondPage);

        // Then
        assertEquals(3, firstPageResult.getContent().size(), "First page should have 3 products");
        assertEquals(3, secondPageResult.getContent().size(), "Second page should have 3 products");
        assertEquals(7, firstPageResult.getTotalElements(), "Total elements should be 7");
        assertEquals(3, firstPageResult.getTotalPages(), "Total pages should be 3");
    }
}
