package Project_ITSS.controller;

import Project_ITSS.common.entity.Product;
import Project_ITSS.service.ProductViewService;
import Project_ITSS.service.UserViewService;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/product")
public class ProductViewController {

    private final ProductViewService productService;
    private final UserViewService userService;

    @Autowired
    public ProductViewController(ProductViewService productService, UserViewService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/all-detail/{id}")
    public Product getProductDetailForManager(@PathVariable("id") int id, @RequestParam("type") String type) {
        validateProductId(id);
        return productService.getFullProductDetail(id, type);
    }

    @GetMapping("/detail/{id}")
    public Product getProductDetailForCustomer(@PathVariable("id") int id) {
        validateProductId(id);
        return productService.getBasicProductDetail(id);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    private void validateProductId(int id) {
        if (id <= 0) {
            throw new ValidationException("The product id is invalid");
        }
    }
}