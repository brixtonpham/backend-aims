package Project_ITSS.service;


import Project_ITSS.dto.OrderTrackingInfo;
import Project_ITSS.dto.ProductItem;
import Project_ITSS.entity.Order;
import Project_ITSS.repository.DeliveryInfoRepository;
import Project_ITSS.repository.OrderPlaceOrderRepository;
import Project_ITSS.repository.OrderlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProcessTrackingInfo {
    @Autowired
    private OrderPlaceOrderRepository orderRepository;
    @Autowired
    private OrderlineRepository orderlineRepository;
    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    public OrderTrackingInfo getTrackingOrder(int order_id){
        Optional<Order> orderOpt = orderRepository.getOrderById(order_id);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found: " + order_id);
        }
        Order order = orderOpt.get();
        OrderTrackingInfo orderTrackingInfo = new OrderTrackingInfo();
        orderTrackingInfo.setOrder_id(order.getOrder_id());
        orderTrackingInfo.setCurrent_status(order.getStatus());
        orderTrackingInfo.setOrder_date(order.getOrder_time());
        Map<String,Object> json = new HashMap<>();
        json.put("total_amount",order.getTotal_after_VAT());
        String address = deliveryInfoRepository.getCustomerAddress(order_id);
        json.put("payment_method",order.getPayment_method());
        json.put("delivery_address",address);
        List<ProductItem> orderlines = orderlineRepository.getProductItemByOrderId(order_id);
        json.put("items",orderlines);
        orderTrackingInfo.setOrder_details(json);
        return orderTrackingInfo;
    }
}
