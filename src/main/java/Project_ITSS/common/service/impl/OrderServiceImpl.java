package Project_ITSS.common.service.impl;

import Project_ITSS.entity.Order;
import Project_ITSS.common.repository.IOrderRepository;
import Project_ITSS.common.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("mainOrderService")
@Primary
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.saveOrder(order);
    }

    @Override
    public Optional<Order> getOrderById(long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public void updateOrder(Order order) {
        orderRepository.updateOrder(order);
    }

    @Override
    public void cancelOrder(long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public boolean existsById(long orderId) {
        return orderRepository.existsById(orderId);
    }
}