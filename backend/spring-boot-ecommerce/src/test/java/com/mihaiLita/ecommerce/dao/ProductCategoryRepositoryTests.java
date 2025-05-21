package com.mihaiLita.ecommerce.dao;

import com.mihaiLita.ecommerce.entity.Product;
import com.mihaiLita.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ProductCategoryRepositoryTests {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        // Clear existing data
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();

        // Create test categories
        ProductCategory electronics = new ProductCategory();
        electronics.setCategoryName("Electronics");

        ProductCategory books = new ProductCategory();
        books.setCategoryName("Books");

        ProductCategory clothing = new ProductCategory();
        clothing.setCategoryName("Clothing");

        ProductCategory furniture = new ProductCategory();
        furniture.setCategoryName("Furniture");

        // Save all categories
        productCategoryRepository.saveAll(Arrays.asList(electronics, books, clothing, furniture));
    }

    @Test
    public void testFindAll() {
        // When
        List<ProductCategory> categories = productCategoryRepository.findAll();

        // Then
        assertEquals(4, categories.size(), "Should find all 4 categories");
        assertTrue(categories.stream().anyMatch(c -> c.getCategoryName().equals("Electronics")));
        assertTrue(categories.stream().anyMatch(c -> c.getCategoryName().equals("Books")));
        assertTrue(categories.stream().anyMatch(c -> c.getCategoryName().equals("Clothing")));
        assertTrue(categories.stream().anyMatch(c -> c.getCategoryName().equals("Furniture")));
    }

    @Test
    public void testFindAllWithSorting() {
        // When
        List<ProductCategory> categoriesAsc = productCategoryRepository.findAll(
                Sort.by(Sort.Direction.ASC, "categoryName"));
        List<ProductCategory> categoriesDesc = productCategoryRepository.findAll(
                Sort.by(Sort.Direction.DESC, "categoryName"));

        // Then
        assertEquals("Books", categoriesAsc.get(0).getCategoryName(),
                "First category should be Books when sorted ascending");
        assertEquals("Furniture", categoriesDesc.get(0).getCategoryName(),
                "First category should be Furniture when sorted descending");
    }

    @Test
    public void testFindById() {
        // Given
        List<ProductCategory> categories = productCategoryRepository.findAll();
        ProductCategory electronics = categories.stream()
                .filter(c -> c.getCategoryName().equals("Electronics"))
                .findFirst()
                .orElseThrow();

        // When
        Optional<ProductCategory> found = productCategoryRepository.findById(electronics.getId());

        // Then
        assertTrue(found.isPresent(), "Should find the category by ID");
        assertEquals("Electronics", found.get().getCategoryName());
    }

    @Test
    public void testFindByIdNotFound() {
        // When
        Optional<ProductCategory> notFound = productCategoryRepository.findById(999L);

        // Then
        assertFalse(notFound.isPresent(), "Should not find a category with non-existent ID");
    }

    @Test
    public void testSave() {
        // Given
        ProductCategory newCategory = new ProductCategory();
        newCategory.setCategoryName("Sports");

        // When
        ProductCategory saved = productCategoryRepository.save(newCategory);

        // Then
        assertNotNull(saved.getId(), "Saved category should have an ID");
        assertEquals("Sports", saved.getCategoryName());

        // Verify it can be retrieved
        Optional<ProductCategory> retrieved = productCategoryRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Sports", retrieved.get().getCategoryName());
    }

    @Test
    public void testUpdate() {
        // Given
        List<ProductCategory> categories = productCategoryRepository.findAll();
        ProductCategory booksCategory = categories.stream()
                .filter(c -> c.getCategoryName().equals("Books"))
                .findFirst()
                .orElseThrow();

        // When
        booksCategory.setCategoryName("Academic Books");
        ProductCategory updated = productCategoryRepository.save(booksCategory);

        // Then
        assertEquals("Academic Books", updated.getCategoryName(), "Category name should be updated");

        // Verify it's updated in database
        Optional<ProductCategory> retrieved = productCategoryRepository.findById(booksCategory.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Academic Books", retrieved.get().getCategoryName());
    }

    @Test
    public void testDelete() {
        // Given
        List<ProductCategory> categories = productCategoryRepository.findAll();
        ProductCategory clothingCategory = categories.stream()
                .filter(c -> c.getCategoryName().equals("Clothing"))
                .findFirst()
                .orElseThrow();
        Long categoryId = clothingCategory.getId();

        // When
        productCategoryRepository.delete(clothingCategory);

        // Then
        Optional<ProductCategory> deleted = productCategoryRepository.findById(categoryId);
        assertFalse(deleted.isPresent(), "Category should be deleted");

        // Verify count is reduced
        assertEquals(3, productCategoryRepository.count(), "Should have 3 categories after deletion");
    }

    @Test
    public void testDeleteById() {
        // Given
        List<ProductCategory> categories = productCategoryRepository.findAll();
        ProductCategory furnitureCategory = categories.stream()
                .filter(c -> c.getCategoryName().equals("Furniture"))
                .findFirst()
                .orElseThrow();
        Long categoryId = furnitureCategory.getId();

        // When
        productCategoryRepository.deleteById(categoryId);

        // Then
        Optional<ProductCategory> deleted = productCategoryRepository.findById(categoryId);
        assertFalse(deleted.isPresent(), "Category should be deleted by ID");
    }

    @Test
    public void testCount() {
        // When
        long count = productCategoryRepository.count();

        // Then
        assertEquals(4, count, "Should count 4 categories");
    }

    @Test
    public void testExistsById() {
        // Given
        List<ProductCategory> categories = productCategoryRepository.findAll();
        ProductCategory electronicsCategory = categories.stream()
                .filter(c -> c.getCategoryName().equals("Electronics"))
                .findFirst()
                .orElseThrow();
        Long categoryId = electronicsCategory.getId();

        // When & Then
        assertTrue(productCategoryRepository.existsById(categoryId), "Category should exist");
        assertFalse(productCategoryRepository.existsById(999L), "Non-existent category should return false");
    }

    @Test
    public void testProductCategoryRelationship() {
        // Given
        // Get a fresh instance of a category from the database
        ProductCategory electronics = productCategoryRepository.findAll().stream()
                .filter(c -> c.getCategoryName().equals("Electronics"))
                .findFirst()
                .orElseThrow();

        // Create and save a product associated with the category
        Product product = new Product();
        product.setSku("TEST-001");
        product.setName("Test Product");
        product.setDescription("Test product for category relationship");
        product.setUnitPrice(new BigDecimal("99.99"));
        product.setImageUrl("test.png");
        product.setActive(true);
        product.setUnitsInStock(10);
        product.setCategory(electronics);

        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();

        // Verify product was saved with correct category
        Optional<Product> retrievedProduct = productRepository.findById(productId);
        assertTrue(retrievedProduct.isPresent(), "Product should be saved");
        assertEquals(electronics.getId(), retrievedProduct.get().getCategory().getId(),
                "Product should reference the correct category");

        // Test fetching products by category ID
        // This uses your ProductRepository.findByCategoryId method
        Page<Product> productsByCategory = productRepository.findByCategoryId(
                electronics.getId(), PageRequest.of(0, 10));
        assertTrue(productsByCategory.getTotalElements() > 0,
                "Should find products in the electronics category");
        assertTrue(productsByCategory.getContent().stream()
                        .anyMatch(p -> p.getId().equals(productId)),
                "Should find our newly created product");
    }

    @Test
    public void testDeleteCategoryWithoutCascade() {
        // Given
        ProductCategory category = new ProductCategory();
        category.setCategoryName("Temporary Category");
        ProductCategory savedCategory = productCategoryRepository.save(category);

        // When - Try to delete the category
        productCategoryRepository.delete(savedCategory);

        // Then
        assertFalse(productCategoryRepository.existsById(savedCategory.getId()),
                "Category should be deleted");
    }
}