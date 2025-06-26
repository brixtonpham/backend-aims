package Project_ITSS.controller;

import Project_ITSS.dto.OrderDetailResponse;
import Project_ITSS.dto.OrderInfo;
import Project_ITSS.dto.OrderTrackingInfo;
import Project_ITSS.service.OrderService_PlaceOrder;
import Project_ITSS.service.ProcessTrackingInfo;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class OrderManagementController {
    
    private final ProcessTrackingInfo processTrackingInfo;
    private final OrderService_PlaceOrder orderService;

    @Autowired
    public OrderManagementController(ProcessTrackingInfo processTrackingInfo, OrderService_PlaceOrder orderService) {
        this.processTrackingInfo = processTrackingInfo;
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderInfo>> getAllOrders() {
        try {
            List<OrderInfo> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            throw new ValidationException("Failed to retrieve orders: " + e.getMessage());
        }
    }

    @GetMapping("/order-detail")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@RequestParam("order_id") int orderId) {
        if (orderId <= 0) {
            throw new ValidationException("Order ID must be positive");
        }
        
        try {
            OrderTrackingInfo order = processTrackingInfo.getTrackingOrder(orderId);
            OrderDetailResponse response = new OrderDetailResponse(1, "Order details successfully retrieved", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            OrderDetailResponse response = new OrderDetailResponse(0, "Failed to retrieve order details: " + e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}