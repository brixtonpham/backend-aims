package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;

/**
 * Interface for product detail update repositories using flattened structure
 * Follows Interface Segregation Principle
 */
public interface DetailProductUpdateRepository {
    void updateProductInfo(Product product);
    String getType();
}