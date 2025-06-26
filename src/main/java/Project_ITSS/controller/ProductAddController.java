package Project_ITSS.controller;

import Project_ITSS.common.entity.Product;
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
        try {
            if (product == null) {
                throw new ValidationException("Product cannot be null");
            }
            
            // Delegate business logic to service layer
            int productId = productService.addProduct(product);
            
            // Log the successful operation - handle logger exceptions gracefully
            try {
                loggerService.saveLogger(product);
            } catch (Exception e) {
                // Continue execution even if logging fails
                // In a real application, you might want to log this failure
            }
            
            return ResponseEntity.ok(ProductResponse.success(productId));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(ProductResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ProductResponse.error("Internal server error", "INTERNAL_ERROR"));
        }
    }

    @GetMapping("/adding/available")
    public ResponseEntity<Boolean> checkValidAdding() {
        try {
            return ResponseEntity.ok(loggerService.checkValidAddProducts());
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}