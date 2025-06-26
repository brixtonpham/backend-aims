package Project_ITSS.service;

import Project_ITSS.dto.OrderInfo;
import Project_ITSS.entity.Order;
import Project_ITSS.common.service.IOrderService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.repository.OrderPlaceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("placeOrderService")
public class OrderPlaceOrderService implements IOrderService {
    
    private final OrderPlaceOrderRepository orderRepository;

    @Autowired
    public OrderPlaceOrderService(OrderPlaceOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void saveOrder(Order order) {
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        orderRepository.saveOrder(order);
    }

    @Override
    public Optional<Order> getOrderById(long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.getOrdersByStatus(status);
    }

    @Override
    public void updateOrder(Order order) {
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        orderRepository.updateOrder(order);
    }

    @Override
    public void cancelOrder(long orderId) {
        orderRepository.cancelOrder(orderId);
    }

    @Override
    public boolean existsById(long orderId) {
        return orderRepository.existsById(orderId);
    }

    public List<OrderInfo> getAllOrderInfos() {
        return orderRepository.getAllOrderInfos();
    }
}