package Project_ITSS.service;

import Project_ITSS.dto.OrderInfo;
import Project_ITSS.entity.*;
import Project_ITSS.repository.OrderPlaceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService_PlaceOrder {
    @Autowired
    private OrderPlaceOrderRepository orderRepository;

    public void saveOrder(Order order){
        orderRepository.saveOrder(order);
    }

    public List<OrderInfo> getAllOrders(){
         return orderRepository.getAllOrderInfos();
    }


    // ... các method khác cho order thường
} 