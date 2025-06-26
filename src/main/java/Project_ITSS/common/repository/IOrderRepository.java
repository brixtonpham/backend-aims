package Project_ITSS.common.repository;

import Project_ITSS.entity.Order;
import java.util.List;
import java.util.Optional;

public interface IOrderRepository {
    
    void saveOrder(Order order);
    
    Optional<Order> findById(long orderId);
    
    List<Order> findAll();
    
    List<Order> findByStatus(String status);
    
    void updateOrder(Order order);
    
    void deleteById(long orderId);
    
    boolean existsById(long orderId);
    
    List<Order> findByDeliveryId(int deliveryId);
}