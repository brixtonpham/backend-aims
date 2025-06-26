package Project_ITSS.common.repository;

import Project_ITSS.common.entity.Product;
import java.util.List;
import java.util.Optional;

public interface IProductRepository {
    
    int insertProductInfo(Product product);
    
    void updateProduct(Product product);
    
    Optional<Product> findById(long productId);
    
    List<Product> findAll();
    
    List<Product> findByType(String type);
    
    boolean checkProductValidity(int quantity, long productId);
    
    void deleteById(long productId);
    
    boolean existsById(long productId);
    
    List<Product> findByTitleContaining(String title);
}