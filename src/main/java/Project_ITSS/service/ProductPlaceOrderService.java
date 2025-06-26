package Project_ITSS.service;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.service.IProductService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.repository.ProductPlaceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("placeOrderProductService")
public class ProductPlaceOrderService implements IProductService {
    
    private final ProductPlaceOrderRepository productRepository;

    @Autowired
    public ProductPlaceOrderService(ProductPlaceOrderRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public int addProduct(Product product) {
        throw new UnsupportedOperationException("Product addition not supported in PlaceOrder service");
    }

    @Override
    public void updateProduct(Product product) {
        throw new UnsupportedOperationException("Product update not supported in PlaceOrder service");
    }

    @Override
    public Optional<Product> getProductById(long productId) {
        return productRepository.getProductById(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    @Override
    public List<Product> getProductsByType(String type) {
        return productRepository.getProductsByType(type);
    }

    @Override
    public boolean checkProductValidity(int quantity, long productId) {
        if (quantity <= 0) {
            throw new ValidationException("The quantity of product is invalid");
        }
        
        int availableQuantity = productRepository.getProductQuantity((int) productId);
        return quantity <= availableQuantity;
    }

    @Override
    public void deleteProduct(long productId) {
        throw new UnsupportedOperationException("Product deletion not supported in PlaceOrder service");
    }

    @Override
    public List<Product> searchProductsByTitle(String title) {
        return productRepository.searchProductsByTitle(title);
    }

    public void updateProductQuantity(int orderId) {
        productRepository.updateProductQuantity(orderId);
    }
}