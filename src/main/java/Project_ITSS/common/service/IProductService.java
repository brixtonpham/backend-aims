package Project_ITSS.common.service;

import Project_ITSS.common.entity.Product;
import java.util.List;
import java.util.Optional;

public interface IProductService {
    
    int addProduct(Product product);
    
    void updateProduct(Product product);
    
    Optional<Product> getProductById(long productId);
    
    List<Product> getAllProducts();
    
    List<Product> getProductsByType(String type);
    
    boolean checkProductValidity(int quantity, long productId);
    
    void deleteProduct(long productId);
    
    List<Product> searchProductsByTitle(String title);
}