package Project_ITSS.common.repository;

import Project_ITSS.entity.Order;
import Project_ITSS.entity.Orderline;
import java.util.List;
import java.util.Optional;

public interface IOrderlineRepository {
    
    void saveOrderlines(Order order);
    
    void saveOrderline(Orderline orderline);
    
    List<Orderline> findByOrderId(long orderId);
    
    Optional<Orderline> findById(long orderlineId);
    
    List<Orderline> findAll();
    
    void updateOrderline(Orderline orderline);
    
    void deleteById(long orderlineId);
    
    void deleteByOrderId(long orderId);
}