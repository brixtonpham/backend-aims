package Project_ITSS.service;

import Project_ITSS.repository.ProductRepository_CancelOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService_CancelOrder {

    @Autowired
    private ProductRepository_CancelOrder productRepository;


    public void updateProductQuantity(int order_id) {
        productRepository.updateProductQuantity(order_id);
    }


}
