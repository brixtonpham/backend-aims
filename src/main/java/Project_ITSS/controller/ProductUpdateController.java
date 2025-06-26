package Project_ITSS.controller;

import Project_ITSS.common.entity.Product;
import Project_ITSS.service.ProductUpdateService;
import Project_ITSS.service.LoggerUpdateService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProductUpdateController {

    private final ProductUpdateService productService;
    private final LoggerUpdateService loggerService;

    @Autowired
    public ProductUpdateController(ProductUpdateService productService, LoggerUpdateService loggerService) {
        this.productService = productService;
        this.loggerService = loggerService;
    }

    @GetMapping("/updating/available")
    public ResponseEntity<Boolean> checkValidUpdating() {
        return ResponseEntity.ok(loggerService.checkValidUpdateProducts());
    }

    @GetMapping("/UpdatingRequested")
    public ResponseEntity<String> requestToUpdateProduct() {
        return ResponseEntity.ok("Product update request received");
    }

    @PostMapping("/updating/ProductInfo")
    public ResponseEntity<ProductResponse> updateProductInfo(@RequestBody Product product) {
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        
        // Delegate business logic to service layer
        int productId = productService.updateProduct(product);
        
        // Log the successful operation
        loggerService.saveLogger(product);
        
        return ResponseEntity.ok(ProductResponse.success(productId));
    }
}