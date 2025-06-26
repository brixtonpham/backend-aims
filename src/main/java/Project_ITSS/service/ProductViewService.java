package Project_ITSS.service;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.repository.DetailProductViewRepository;
import Project_ITSS.repository.ProductViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductViewService {
    
    private final Map<String, DetailProductViewRepository> repositoryMap;
    private final ProductViewRepository productRepository;

    @Autowired
    public ProductViewService(List<DetailProductViewRepository> repositories, 
                             ProductViewRepository productRepository) {
        this.productRepository = productRepository;
        this.repositoryMap = repositories.stream()
            .collect(Collectors.toMap(
                DetailProductViewRepository::getType,
                Function.identity()
            ));
    }

    public boolean checkProductValidity(int quantity, int productId) {
        int availableQuantity = productRepository.getProductQuantity(productId);
        return quantity <= availableQuantity;
    }

    public Product getBasicProductDetail(int id) {
        return productRepository.findById(id);
    }

    public Product getFullProductDetail(int id, String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new ValidationException("Product type is required");
        }
        
        DetailProductViewRepository repo = repositoryMap.get(type.toLowerCase());
        if (repo == null) {
            throw new ValidationException("No repository found for product type: " + type);
        }
        
        return repo.getProductDetailInfo(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }
}