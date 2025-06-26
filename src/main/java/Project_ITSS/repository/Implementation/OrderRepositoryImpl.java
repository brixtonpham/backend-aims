package Project_ITSS.repository.Implementation;

import Project_ITSS.repository.IOrderRepository;
import Project_ITSS.repository.OrderRepository_CancelOrder;
import Project_ITSS.entity.Order;
import Project_ITSS.entity.DeliveryInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of IOrderRepository interface
 * Wraps the existing OrderPlaceOrderRepository
 */
@Repository
public class OrderRepositoryImpl implements IOrderRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryImpl.class);
    
    private final OrderRepository_CancelOrder orderRepository;
    
    @Autowired
    public OrderRepositoryImpl(OrderRepository_CancelOrder orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public Order findById(long orderId) {
        logger.debug("Finding order by ID: {}", orderId);
        try {
            return orderRepository.getOrderById(orderId);
        } catch (Exception e) {
            logger.debug("Order not found with ID: {}", orderId);
            return null;
        }
    }
    
    @Override
    public void updateStatus(long orderId, String status) {
        logger.debug("Updating order {} status to: {}", orderId, status);
        orderRepository.updateOrderStatus(orderId, status);
    }
    
    @Override
    public DeliveryInformation getDeliveryInfo(long deliveryId) {
        logger.debug("Getting delivery info for ID: {}", deliveryId);
        try {
            return orderRepository.getDeliveryInformationById(deliveryId);
        } catch (Exception e) {
            logger.debug("Delivery information not found with ID: {}", deliveryId);
            return null;
        }
    }
    
    @Override
    public boolean existsById(long orderId) {
        logger.debug("Checking if order exists: {}", orderId);
        try {
            Order order = orderRepository.getOrderById(orderId);
            return order != null;
        } catch (Exception e) {
            logger.debug("Order not found with ID: {}", orderId);
            return false;
        }
    }
    
    @Override
    public String getOrderStatus(long orderId) {
        logger.debug("Getting order status for ID: {}", orderId);
        try {
            Order order = orderRepository.getOrderById(orderId);
            return order != null ? order.getStatus() : null;
        } catch (Exception e) {
            logger.debug("Order not found with ID: {}", orderId);
            return null;
        }
    }
} 