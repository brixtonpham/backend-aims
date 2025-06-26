package Project_ITSS.service;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.validation.ProductValidationService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.repository.DetailProductUpdateRepository;
import Project_ITSS.repository.ProductUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductUpdateService {
    
    private final ProductUpdateRepository productRepository;
    private final ProductValidationService validationService;
    private final Map<String, DetailProductUpdateRepository> repositoryMap;

    @Autowired
    public ProductUpdateService(ProductUpdateRepository productRepository,
                               ProductValidationService validationService,
                               List<DetailProductUpdateRepository> repositories) {
        this.productRepository = productRepository;
        this.validationService = validationService;
        this.repositoryMap = repositories.stream()
            .collect(Collectors.toMap(
                DetailProductUpdateRepository::getType,
                Function.identity()
            ));
    }

    @Transactional
    public int updateProduct(Product product) {
        try {
            // Validate product before processing using common validation
            validationService.validateForUpdate(product);
            
            // Update basic product information
            updateProductInfo(product);
            
            // Update type-specific details
            updateProductDetail(product, product.getType());
            
            return (int) product.getProductId();
        } catch (ValidationException e) {
            throw e; // Re-throw validation exceptions as-is
        } catch (Exception e) {
            throw new ValidationException("Unexpected error occurred while updating product: " + e.getMessage(), e);
        }
    }

    private void updateProductInfo(Product product) {
        try {
            productRepository.updateProductInfo(product);
        } catch (Exception e) {
            throw new ValidationException("Failed to update product information: " + e.getMessage(), e);
        }
    }

    private void updateProductDetail(Product product, String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("Product type is required for update");
        }
        
        DetailProductUpdateRepository repo = repositoryMap.get(type.toLowerCase());
        if (repo == null) {
            throw new ValidationException("Unsupported product type: " + type);
        }
        
        try {
            repo.updateProductInfo(product);
        } catch (Exception e) {
            throw new ValidationException("Failed to update product detail for type: " + type + ". " + e.getMessage(), e);
        }
    }
}