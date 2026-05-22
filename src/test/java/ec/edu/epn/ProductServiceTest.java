
package ec.edu.epn;

import ec.edu.epn.model.Category;
import ec.edu.epn.model.Product;
import ec.edu.epn.repository.ProductRepository;
import ec.edu.epn.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }
    // findBySKu
    @Test
    void findBySku_whenProductExists_returnsProduct() {
        Category cat = new Category("CatName", "Desc");
        Product p = new Product("SKU123", "ProductName", BigDecimal.valueOf(9.99), 10, cat);
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(p));

        Product result = productService.findBySku("SKU123");
        assertSame(p, result);
    }
    @Test
    void findBySku_whenNotExists_throwsRuntimeException() {
        when(productRepository.findBySku("NO_EXISTE")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.findBySku("NO_EXISTE"));
        assertTrue(ex.getMessage().contains("NO_EXISTE"));
    }

    // findActiveProducts
    @Test
    void findActiveProducts_whenThereAreActiveProducts_returnsList() {
        Category cat = new Category("Cat", "Desc");
        Product p1 = new Product("SKU1", "Product1", BigDecimal.valueOf(10), 5, cat);
        p1.setActive(true);
        Product p2 = new Product("SKU2", "Product2", BigDecimal.valueOf(20), 3, cat);
        p2.setActive(true);

        when(productRepository.findByActiveTrue()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.findActiveProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(p1, result.get(0));
        assertSame(p2, result.get(1));
        verify(productRepository).findByActiveTrue();
    }

    @Test
    void findActiveProducts_whenNoActiveProducts_returnsEmptyList() {
        when(productRepository.findByActiveTrue()).thenReturn(List.of());

        List<Product> result = productService.findActiveProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findByActiveTrue();
    }


}