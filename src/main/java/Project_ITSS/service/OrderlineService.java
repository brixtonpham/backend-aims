package Project_ITSS.service;

import Project_ITSS.entity.Order;
import Project_ITSS.entity.Orderline;
import Project_ITSS.common.service.IOrderlineService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.repository.OrderlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderlineService implements IOrderlineService {
    
    private final OrderlineRepository orderlineRepository;

    @Autowired
    public OrderlineService(OrderlineRepository orderlineRepository) {
        this.orderlineRepository = orderlineRepository;
    }

    @Override
    public void saveOrderlines(Order order) {
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        
        for (Orderline orderline : order.getOrderLineList()) {
            orderlineRepository.saveOrderline(orderline);
        }
    }

    @Override
    public void saveOrderline(Orderline orderline) {
        if (orderline == null) {
            throw new ValidationException("Orderline cannot be null");
        }
        orderlineRepository.saveOrderline(orderline);
    }

    @Override
    public List<Orderline> getOrderlinesByOrderId(long orderId) {
        return orderlineRepository.getOrderlinesByOrderId(orderId);
    }

    @Override
    public void updateOrderline(Orderline orderline) {
        if (orderline == null) {
            throw new ValidationException("Orderline cannot be null");
        }
        orderlineRepository.updateOrderline(orderline);
    }

    @Override
    public void deleteOrderlinesByOrderId(long orderId) {
        orderlineRepository.deleteOrderlinesByOrderId(orderId);
    }
}