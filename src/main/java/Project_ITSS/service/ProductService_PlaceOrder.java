package Project_ITSS.service;


import Project_ITSS.exception.PlaceOrderException;
import Project_ITSS.repository.ProductPlaceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService_PlaceOrder {

    @Autowired
    private ProductPlaceOrderRepository productRepository;

    public boolean checkProductValidity(int quantity,int product_id){
        if(quantity <= 0){
            throw new PlaceOrderException("The quantity of product is invalid");
        }
        int available_quantity = productRepository.getProductQuantity(product_id);
        if (quantity > available_quantity) return false;
        else return true;
    }

    public void updateProductQuantity(int order_id) {
        productRepository.updateProductQuantity(order_id);
    }
}
