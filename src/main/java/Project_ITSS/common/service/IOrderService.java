package Project_ITSS.common.service;

import Project_ITSS.entity.Order;
import java.util.List;
import java.util.Optional;

public interface IOrderService {
    
    void saveOrder(Order order);
    
    Optional<Order> getOrderById(long orderId);
    
    List<Order> getAllOrders();
    
    List<Order> getOrdersByStatus(String status);
    
    void updateOrder(Order order);
    
    void cancelOrder(long orderId);
    
    boolean existsById(long orderId);
}