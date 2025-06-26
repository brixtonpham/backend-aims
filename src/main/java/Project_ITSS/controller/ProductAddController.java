package Project_ITSS.controller;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.entity.Book;
import Project_ITSS.service.ProductAddService;
import Project_ITSS.service.LoggerAddService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProductAddController {

    private final ProductAddService productService;
    private final LoggerAddService loggerService;

    @Autowired
    public ProductAddController(ProductAddService productService, LoggerAddService loggerService) {
        this.productService = productService;
        this.loggerService = loggerService;
    }

    @GetMapping("/AddingRequested")
    public ResponseEntity<String> requestToAddProduct() {
        return ResponseEntity.ok("Product addition request received");
    }

    @PostMapping("/adding/ProductInfo")
    public ResponseEntity<ProductResponse> submitProductInfo(@RequestBody Product product) {
        if (product == null) {
            throw new ValidationException("Product cannot be null");
        }
        
        // Delegate business logic to service layer
        int productId = productService.addProduct(product);
        
        // Log the successful operation
        loggerService.saveLogger(product);
        
        return ResponseEntity.ok(ProductResponse.success(productId));
    }

    @GetMapping("/adding/available")
    public ResponseEntity<Boolean> checkValidAdding() {
        return ResponseEntity.ok(loggerService.checkValidAddProducts());
    }
}