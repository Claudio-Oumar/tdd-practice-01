
package ec.edu.epn.service;

import ec.edu.epn.model.Product;
import ec.edu.epn.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product with SKU " + sku + " not found"));
    }

    public List<Product> findActiveProducts() {
        return productRepository.findByActiveTrue();
    }
}