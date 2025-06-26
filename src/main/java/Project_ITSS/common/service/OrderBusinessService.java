package Project_ITSS.common.service;

import Project_ITSS.dto.CalculateFeeDTO;
import Project_ITSS.dto.Order_DeliveryInfo;
import Project_ITSS.entity.*;
import Project_ITSS.exception.PlaceOrderException;
import Project_ITSS.common.entity.Product;
import Project_ITSS.service.IDeliveryFeeCalculating;
import Project_ITSS.service.InfoValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderBusinessService {

    @Value("${calculating_service_version}")
    private String calculatingServiceVersion;

    private final Map<String, IDeliveryFeeCalculating> deliveryFeeCalculatingsMap = new HashMap<>();

    private final IProductService productService;
    private final IOrderService orderService;
    private final IEmailNotificationService emailNotificationService;
    private final IDeliveryInfoService deliveryInfoService;
    private final IOrderlineService orderlineService;
    
    @Autowired
    private InfoValidationService infoValidationService;

    @Autowired
    public OrderBusinessService(
            List<IDeliveryFeeCalculating> deliveryFeeCalculatings,
            IProductService productService,
            IOrderService orderService,
            IEmailNotificationService emailNotificationService,
            IDeliveryInfoService deliveryInfoService,
            IOrderlineService orderlineService) {
        
        this.productService = productService;
        this.orderService = orderService;
        this.emailNotificationService = emailNotificationService;
        this.deliveryInfoService = deliveryInfoService;
        this.orderlineService = orderlineService;
        
        for (IDeliveryFeeCalculating service : deliveryFeeCalculatings) {
            deliveryFeeCalculatingsMap.put(service.getVersion(), service);
        }
    }

    public Order validateAndCreateOrder(Cart cart) {
        if (cart == null) {
            throw new PlaceOrderException("The cart is null");
        }

        validateCartProducts(cart);
        
        Order order = new Order();
        order.createOrder(cart);
        return order;
    }

    private void validateCartProducts(Cart cart) {
        for (CartItem cartItem : cart.getProducts()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            
            if (product == null) {
                throw new PlaceOrderException("Product is null");
            }
            
            boolean isValid = productService.checkProductValidity(quantity, product.getProductId());
            if (!isValid) {
                throw new PlaceOrderException("Inadequate product quantity for product ID: " + product.getProductId());
            }
        }
    }

    public DeliveryInformation validateAndCreateDeliveryInfo(DeliveryInformation deliveryInfo) {
        infoValidationService.validateDeliveryInfoStringLength(deliveryInfo);
        
        DeliveryInformation validatedDeliveryInfo = new DeliveryInformation();
        validatedDeliveryInfo.createDeliveryInfo(
            deliveryInfo.getName(),
            deliveryInfo.getPhone(),
            deliveryInfo.getEmail(),
            deliveryInfo.getAddress(),
            deliveryInfo.getProvince(),
            deliveryInfo.getDelivery_message(),
            deliveryInfo.getDelivery_fee()
        );
        
        return validatedDeliveryInfo;
    }

    public int[] calculateDeliveryFee(CalculateFeeDTO feeInfoDTO) {
        IDeliveryFeeCalculating service = deliveryFeeCalculatingsMap.get(calculatingServiceVersion);
        if (service == null) {
            throw new PlaceOrderException("Delivery fee calculation service not found for version: " + calculatingServiceVersion);
        }
        
        return service.CalculateDeliveryFee(feeInfoDTO);
    }

    @Transactional
    public void completeOrderPlacement(Order_DeliveryInfo orderInfoDTO) {
        DeliveryInformation deliveryInformation = orderInfoDTO.getDeliveryInformation();
        Order order = orderInfoDTO.getOrder();
        
        // Save delivery information
        int deliveryId = deliveryInfoService.saveDeliveryInfo(deliveryInformation);
        order.setDelivery_id((long) deliveryId);
        
        // Save order and order lines
        orderService.saveOrder(order);
        orderlineService.saveOrderlines(order);
        
        // Send confirmation email
        sendOrderConfirmationEmail(deliveryInformation, order);
    }

    private void sendOrderConfirmationEmail(DeliveryInformation deliveryInfo, Order order) {
        String subject = "Thông báo về việc đặt hàng";
        String message = "Bạn đã đặt hàng thành công. Mã đơn hàng của bạn là: " + order.getOrder_id();
        emailNotificationService.sendSuccessEmail(deliveryInfo.getEmail(), subject, message);
    }
}