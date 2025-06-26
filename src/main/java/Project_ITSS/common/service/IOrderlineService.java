package Project_ITSS.common.service;

import Project_ITSS.entity.Order;
import Project_ITSS.entity.Orderline;
import java.util.List;

public interface IOrderlineService {
    
    void saveOrderlines(Order order);
    
    void saveOrderline(Orderline orderline);
    
    List<Orderline> getOrderlinesByOrderId(long orderId);
    
    void updateOrderline(Orderline orderline);
    
    void deleteOrderlinesByOrderId(long orderId);
}