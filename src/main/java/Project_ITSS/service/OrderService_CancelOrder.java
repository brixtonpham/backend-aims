package Project_ITSS.service;

import Project_ITSS.entity.DeliveryInfo;
import Project_ITSS.entity.DeliveryInformation;
import Project_ITSS.entity.Order;
import Project_ITSS.entity.Orderline;
import Project_ITSS.repository.OrderRepository_CancelOrder;
import Project_ITSS.repository.ProductRepository_CancelOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService_CancelOrder {

    @Autowired
    private OrderRepository_CancelOrder orderRepository;

    public Order getOrderById(long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public void updateOrderStatusToApprove(long orderId) {
        orderRepository.updateOrderStatusToApprove(orderId);
    }

} 