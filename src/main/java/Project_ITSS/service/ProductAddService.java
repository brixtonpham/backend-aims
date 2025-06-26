package Project_ITSS.service;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.validation.ProductValidationService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.repository.DetailProductAddRepository;
import Project_ITSS.repository.ProductAddRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductAddService {
    
    private final ProductAddRepository productRepository;
    private final ProductValidationService validationService;
    private final Map<String, DetailProductAddRepository> repositoryMap;

    @Autowired
    public ProductAddService(ProductAddRepository productRepository,
                            List<DetailProductAddRepository> repositories,
                            ProductValidationService validationService) {
        this.productRepository = productRepository;
        this.validationService = validationService;
        this.repositoryMap = repositories.stream()
            .collect(Collectors.toMap(
                DetailProductAddRepository::getType,
                Function.identity()
            ));
    }

    @Transactional
    public int addProduct(Product product) {
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        
        try {
            // Validate product before processing using common validation
            validationService.validateForCreation(product);
            
            // Insert basic product information
            int productId = insertProductInfo(product);
            if (productId <= 0) {
                throw new ValidationException("Failed to insert product information");
            }
            
            // Set the product ID and insert type-specific details
            product.setProductId(productId);
            insertProductDetail(product, product.getType());
            
            return productId;
        } catch (ValidationException e) {
            throw e; // Re-throw validation exceptions as-is
        } catch (Exception e) {
            throw new ValidationException("Unexpected error occurred while adding product: " + e.getMessage(), e);
        }
    }

    private int insertProductInfo(Product product) {
        try {
            return productRepository.insertProductInfo(product);
        } catch (Exception e) {
            throw new ValidationException("Failed to insert product information: " + e.getMessage(), e);
        }
    }

    private void insertProductDetail(Product product, String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("Product type is required for creation");
        }
        
        DetailProductAddRepository repo = repositoryMap.get(type.toLowerCase());
        if (repo == null) {
            throw new ValidationException("No repository found for product type: " + type);
        }
        
        try {
            repo.insertProductInfo(product);
        } catch (Exception e) {
            throw new ValidationException("Failed to insert product detail for type: " + type + ". " + e.getMessage(), e);
        }
    }
}