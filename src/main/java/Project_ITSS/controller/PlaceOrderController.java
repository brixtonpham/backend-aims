package Project_ITSS.controller;

import Project_ITSS.dto.*;
import Project_ITSS.entity.*;
import Project_ITSS.exception.PlaceOrderException;
import Project_ITSS.common.service.OrderBusinessService;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PlaceOrderController {

    private final OrderBusinessService orderBusinessService;

    @Autowired
    public PlaceOrderController(OrderBusinessService orderBusinessService) {
        this.orderBusinessService = orderBusinessService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test successful");
    }

    @PostMapping("/placeorder")
    public ResponseEntity<PlaceOrderResponse> requestToPlaceOrder(@RequestBody Cart cart) {
        if (cart == null) {
            throw new ValidationException("Cart cannot be null");
        }
        
        try {
            Order order = orderBusinessService.validateAndCreateOrder(cart);
            PlaceOrderResponse response = new PlaceOrderResponse(order, "Successfully");
            return ResponseEntity.ok(response);
        } catch (PlaceOrderException e) {
            return ResponseEntity.badRequest().body(new PlaceOrderResponse(null, e.getMessage()));
        }
    }

    @PostMapping("/deliveryinfo")
    public ResponseEntity<DeliveryInfoResponse> submitDeliveryInformation(@RequestBody DeliveryInformation deliveryInfo) {
        if (deliveryInfo == null) {
            throw new ValidationException("Delivery information cannot be null");
        }
        
        DeliveryInformation validatedDeliveryInfo = orderBusinessService.validateAndCreateDeliveryInfo(deliveryInfo);
        DeliveryInfoResponse response = new DeliveryInfoResponse(validatedDeliveryInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recalculate")
    public ResponseEntity<RecalculateResponse> recalculateDeliveryFee(@RequestBody CalculateFeeDTO feeInfoDTO) {
        if (feeInfoDTO == null) {
            throw new ValidationException("Fee information cannot be null");
        }
        
        int[] deliveryFees = orderBusinessService.calculateDeliveryFee(feeInfoDTO);
        int totalDeliveryFee = deliveryFees[0] + deliveryFees[1];
        RecalculateResponse response = new RecalculateResponse(deliveryFees[0], deliveryFees[1], totalDeliveryFee);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/finish-order")
    public ResponseEntity<FinishOrderResponse> finishPlaceOrder(@RequestBody Order_DeliveryInfo orderInfoDTO) {
        if (orderInfoDTO == null) {
            throw new ValidationException("Order information cannot be null");
        }
        
        orderBusinessService.completeOrderPlacement(orderInfoDTO);
        FinishOrderResponse response = new FinishOrderResponse(1);
        return ResponseEntity.ok(response);
    }
}