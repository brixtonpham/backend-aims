package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;

/**
 * Interface for product detail add repositories using flattened structure
 * Follows Interface Segregation Principle
 */
public interface DetailProductAddRepository {
    void insertProductInfo(Product product);
    String getType();
}