package Project_ITSS.repository;

import Project_ITSS.common.entity.Product;

/**
 * Interface for product detail repositories using flattened structure
 * Follows Interface Segregation Principle
 */
public interface DetailProductViewRepository {
    Product getProductDetailInfo(int productId);
    String getType();
}