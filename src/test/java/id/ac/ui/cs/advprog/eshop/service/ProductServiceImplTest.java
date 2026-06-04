package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testCreateProductWithoutId() {
        Product product = new Product();
        product.setProductName("Laptop");
        product.setProductQuantity(10);

        Product result = productService.create(product);

        assertNotNull(result.getProductId());
        assertFalse(result.getProductId().isBlank());

        verify(productRepository, times(1)).create(product);
    }

    @Test
    void testCreateProductWithExistingId() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Laptop");
        product.setProductQuantity(10);

        Product result = productService.create(product);

        assertEquals("product-1", result.getProductId());

        verify(productRepository, times(1)).create(product);
    }

    @Test
    void testFindAllProducts() {
        Product product1 = new Product();
        product1.setProductId("1");
        product1.setProductName("Laptop");
        product1.setProductQuantity(10);

        Product product2 = new Product();
        product2.setProductId("2");
        product2.setProductName("Mouse");
        product2.setProductQuantity(20);

        Iterator<Product> iterator =
                Arrays.asList(product1, product2).iterator();

        when(productRepository.findAll()).thenReturn(iterator);

        List<Product> products = productService.findAll();

        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getProductName());
        assertEquals("Mouse", products.get(1).getProductName());

        verify(productRepository).findAll();
    }

    @Test
    void testFindById() {
        Product product = new Product();
        product.setProductId("1");
        product.setProductName("Laptop");
        product.setProductQuantity(10);

        when(productRepository.findById("1")).thenReturn(product);

        Product result = productService.findById("1");

        assertNotNull(result);
        assertEquals("1", result.getProductId());
        assertEquals("Laptop", result.getProductName());

        verify(productRepository).findById("1");
    }

    @Test
    void testUpdateProduct() {
        Product product = new Product();
        product.setProductId("1");
        product.setProductName("Updated Laptop");
        product.setProductQuantity(15);

        when(productRepository.edit(product)).thenReturn(product);

        Product result = productService.update(product);

        assertEquals("Updated Laptop", result.getProductName());
        assertEquals(15, result.getProductQuantity());

        verify(productRepository).edit(product);
    }

    @Test
    void testDeleteByIdSuccess() {
        when(productRepository.delete("1")).thenReturn(true);

        boolean result = productService.deleteById("1");

        assertTrue(result);

        verify(productRepository).delete("1");
    }

    @Test
    void testDeleteByIdFailed() {
        when(productRepository.delete("1")).thenReturn(false);

        boolean result = productService.deleteById("1");

        assertFalse(result);

        verify(productRepository).delete("1");
    }
}