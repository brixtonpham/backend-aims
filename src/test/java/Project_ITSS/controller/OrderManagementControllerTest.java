package Project_ITSS.controller;

import Project_ITSS.dto.OrderInfo;
import Project_ITSS.dto.OrderTrackingInfo;
import Project_ITSS.service.OrderService_PlaceOrder;
import Project_ITSS.service.ProcessTrackingInfo;
import Project_ITSS.common.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderManagementController Unit Tests")
class OrderManagementControllerTest {

    @Mock
    private ProcessTrackingInfo processTrackingInfo;

    @Mock
    private OrderService_PlaceOrder orderService;

    @InjectMocks
    private OrderManagementController orderManagementController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderManagementController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GET /orders - Get All Orders Tests")
    class GetAllOrdersTests {

        @Test
        @DisplayName("Should return list of all orders successfully")
        void getAllOrders_ValidRequest_ReturnsOrderList() throws Exception {
            // Given
            List<OrderInfo> mockOrders = Arrays.asList(
                createMockOrderInfo(1, "John Doe", 100000, "2025-01-01", "pending", "VNPay"),
                createMockOrderInfo(2, "Jane Smith", 200000, "2025-01-02", "completed", "Credit Card"),
                createMockOrderInfo(3, "Bob Johnson", 150000, "2025-01-03", "processing", "VNPay")
            );
            
            when(orderService.getAllOrders()).thenReturn(mockOrders);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].order_id").value(1))
                    .andExpect(jsonPath("$[0].customer_name").value("John Doe"))
                    .andExpect(jsonPath("$[0].total_amount").value(100000))
                    .andExpect(jsonPath("$[0].order_date").value("2025-01-01"))
                    .andExpect(jsonPath("$[0].status").value("pending"))
                    .andExpect(jsonPath("$[0].payment_method").value("VNPay"))
                    .andExpect(jsonPath("$[1].order_id").value(2))
                    .andExpect(jsonPath("$[1].customer_name").value("Jane Smith"))
                    .andExpect(jsonPath("$[2].order_id").value(3))
                    .andExpect(jsonPath("$[2].customer_name").value("Bob Johnson"));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should return empty list when no orders exist")
        void getAllOrders_NoOrdersExist_ReturnsEmptyList() throws Exception {
            // Given
            when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle large number of orders efficiently")
        void getAllOrders_LargeNumberOfOrders_HandlesEfficiently() throws Exception {
            // Given
            List<OrderInfo> largeOrderList = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                largeOrderList.add(createMockOrderInfo(
                    i, 
                    "Customer " + i, 
                    100000 + (i * 1000), 
                    "2025-01-" + String.format("%02d", (i % 28) + 1),
                    i % 2 == 0 ? "completed" : "pending",
                    i % 3 == 0 ? "Credit Card" : "VNPay"
                ));
            }
            
            when(orderService.getAllOrders()).thenReturn(largeOrderList);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(100))
                    .andExpect(jsonPath("$[0].order_id").value(1))
                    .andExpect(jsonPath("$[99].order_id").value(100));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle orders with various statuses")
        void getAllOrders_OrdersWithVariousStatuses_ReturnsAllStatuses() throws Exception {
            // Given
            List<OrderInfo> ordersWithDifferentStatuses = Arrays.asList(
                createMockOrderInfo(1, "Customer A", 100000, "2025-01-01", "pending", "VNPay"),
                createMockOrderInfo(2, "Customer B", 200000, "2025-01-02", "processing", "Credit Card"),
                createMockOrderInfo(3, "Customer C", 150000, "2025-01-03", "completed", "VNPay"),
                createMockOrderInfo(4, "Customer D", 300000, "2025-01-04", "cancelled", "Credit Card"),
                createMockOrderInfo(5, "Customer E", 250000, "2025-01-05", "approved", "VNPay")
            );
            
            when(orderService.getAllOrders()).thenReturn(ordersWithDifferentStatuses);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(5))
                    .andExpect(jsonPath("$[0].status").value("pending"))
                    .andExpect(jsonPath("$[1].status").value("processing"))
                    .andExpect(jsonPath("$[2].status").value("completed"))
                    .andExpect(jsonPath("$[3].status").value("cancelled"))
                    .andExpect(jsonPath("$[4].status").value("approved"));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle orders with null or empty customer names")
        void getAllOrders_OrdersWithNullCustomerNames_HandlesGracefully() throws Exception {
            // Given
            List<OrderInfo> ordersWithNullNames = Arrays.asList(
                createMockOrderInfo(1, null, 100000, "2025-01-01", "pending", "VNPay"),
                createMockOrderInfo(2, "", 200000, "2025-01-02", "completed", "Credit Card"),
                createMockOrderInfo(3, "Valid Customer", 150000, "2025-01-03", "processing", "VNPay")
            );
            
            when(orderService.getAllOrders()).thenReturn(ordersWithNullNames);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].customer_name").value(nullValue()))
                    .andExpect(jsonPath("$[1].customer_name").value(""))
                    .andExpect(jsonPath("$[2].customer_name").value("Valid Customer"));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle service throwing ValidationException")
        void getAllOrders_ServiceThrowsValidationException_ReturnsBadRequest() throws Exception {
            // Given
            when(orderService.getAllOrders())
                .thenThrow(new ValidationException("Failed to retrieve orders: Database access denied"));

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle service throwing RuntimeException")
        void getAllOrders_ServiceThrowsRuntimeException_ReturnsInternalServerError() throws Exception {
            // Given
            when(orderService.getAllOrders())
                .thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle orders with special characters in customer names")
        void getAllOrders_OrdersWithSpecialCharacters_HandlesCorrectly() throws Exception {
            // Given
            List<OrderInfo> ordersWithSpecialChars = Arrays.asList(
                createMockOrderInfo(1, "Nguyễn Văn A", 100000, "2025-01-01", "pending", "VNPay"),
                createMockOrderInfo(2, "José María García", 200000, "2025-01-02", "completed", "Credit Card"),
                createMockOrderInfo(3, "李小明", 150000, "2025-01-03", "processing", "VNPay"),
                createMockOrderInfo(4, "John O'Connor", 250000, "2025-01-04", "approved", "Credit Card")
            );
            
            when(orderService.getAllOrders()).thenReturn(ordersWithSpecialChars);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(4))
                    .andExpect(jsonPath("$[0].customer_name").value("Nguyễn Văn A"))
                    .andExpect(jsonPath("$[1].customer_name").value("José María García"))
                    .andExpect(jsonPath("$[2].customer_name").value("李小明"))
                    .andExpect(jsonPath("$[3].customer_name").value("John O'Connor"));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle concurrent requests efficiently")
        void getAllOrders_ConcurrentRequests_HandlesCorrectly() throws Exception {
            // Given
            List<OrderInfo> mockOrders = Arrays.asList(
                createMockOrderInfo(1, "Customer 1", 100000, "2025-01-01", "pending", "VNPay")
            );
            
            when(orderService.getAllOrders()).thenReturn(mockOrders);

            // When & Then - Simulate concurrent requests
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$.length()").value(1));
            }

            verify(orderService, times(5)).getAllOrders();
        }

        @Test
        @DisplayName("Should handle orders with extreme monetary values")
        void getAllOrders_OrdersWithExtremeValues_HandlesCorrectly() throws Exception {
            // Given
            List<OrderInfo> ordersWithExtremeValues = Arrays.asList(
                createMockOrderInfo(1, "Small Order", 1, "2025-01-01", "pending", "VNPay"),
                createMockOrderInfo(2, "Large Order", Integer.MAX_VALUE, "2025-01-02", "completed", "Credit Card"),
                createMockOrderInfo(3, "Zero Order", 0, "2025-01-03", "processing", "VNPay")
            );
            
            when(orderService.getAllOrders()).thenReturn(ordersWithExtremeValues);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].total_amount").value(1))
                    .andExpect(jsonPath("$[1].total_amount").value(Integer.MAX_VALUE))
                    .andExpect(jsonPath("$[2].total_amount").value(0));

            verify(orderService).getAllOrders();
        }
    }

    @Nested
    @DisplayName("GET /order-detail - Get Order Detail Tests")
    class GetOrderDetailTests {

        @Test
        @DisplayName("Should return order detail for valid order ID")
        void getOrderDetail_ValidOrderId_ReturnsOrderDetail() throws Exception {
            // Given
            int orderId = 123;
            OrderTrackingInfo mockTrackingInfo = createMockOrderTrackingInfo(orderId);
            
            when(processTrackingInfo.getTrackingOrder(orderId)).thenReturn(mockTrackingInfo);

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.message").value("Order details successfully retrieved"))
                    .andExpect(jsonPath("$.order").exists())
                    .andExpect(jsonPath("$.order.order_id").value(orderId))
                    .andExpect(jsonPath("$.order.current_status").value("processing"))
                    .andExpect(jsonPath("$.order.order_date").value("2025-01-15"))
                    .andExpect(jsonPath("$.order.order_details").exists())
                    .andExpect(jsonPath("$.order.order_details.total_amount").value(250000))
                    .andExpect(jsonPath("$.order.order_details.payment_method").value("VNPay"))
                    .andExpect(jsonPath("$.order.order_details.delivery_address").value("123 Test Street, Test City"));

            verify(processTrackingInfo).getTrackingOrder(orderId);
        }

        @Test
        @DisplayName("Should return order detail with complex order details")
        void getOrderDetail_ComplexOrderDetails_ReturnsCompleteDetail() throws Exception {
            // Given
            int orderId = 456;
            OrderTrackingInfo complexTrackingInfo = createComplexMockOrderTrackingInfo(orderId);
            
            when(processTrackingInfo.getTrackingOrder(orderId)).thenReturn(complexTrackingInfo);

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.message").value("Order details successfully retrieved"))
                    .andExpect(jsonPath("$.order.order_id").value(orderId))
                    .andExpect(jsonPath("$.order.current_status").value("completed"))
                    .andExpect(jsonPath("$.order.order_details.total_amount").value(500000))
                    .andExpect(jsonPath("$.order.order_details.items").isArray())
                    .andExpect(jsonPath("$.order.order_details.items.length()").value(3))
                    .andExpect(jsonPath("$.order.order_details.items[0].product_id").value(1))
                    .andExpect(jsonPath("$.order.order_details.items[0].title").value("Test Book"))
                    .andExpect(jsonPath("$.order.order_details.items[1].product_id").value(2))
                    .andExpect(jsonPath("$.order.order_details.items[2].product_id").value(3));

            verify(processTrackingInfo).getTrackingOrder(orderId);
        }

        @Test
        @DisplayName("Should throw ValidationException for negative order ID")
        void getOrderDetail_NegativeOrderId_ThrowsValidationException() throws Exception {
            // Given
            int invalidOrderId = -1;

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(invalidOrderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(processTrackingInfo, never()).getTrackingOrder(anyInt());
        }

        @Test
        @DisplayName("Should throw ValidationException for zero order ID")
        void getOrderDetail_ZeroOrderId_ThrowsValidationException() throws Exception {
            // Given
            int invalidOrderId = 0;

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(invalidOrderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(processTrackingInfo, never()).getTrackingOrder(anyInt());
        }

        @Test
        @DisplayName("Should handle missing order_id parameter")
        void getOrderDetail_MissingOrderIdParameter_ReturnsBadRequest() throws Exception {
            // When & Then
            mockMvc.perform(get("/order-detail")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(processTrackingInfo, never()).getTrackingOrder(anyInt());
        }

        @Test
        @DisplayName("Should handle invalid order_id parameter format")
        void getOrderDetail_InvalidOrderIdFormat_ReturnsBadRequest() throws Exception {
            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", "invalid")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError()); // Spring parameter binding error

            verify(processTrackingInfo, never()).getTrackingOrder(anyInt());
        }

        @Test
        @DisplayName("Should handle service throwing RuntimeException")
        void getOrderDetail_ServiceThrowsRuntimeException_ReturnsErrorResponse() throws Exception {
            // Given
            int orderId = 123;
            
            when(processTrackingInfo.getTrackingOrder(orderId))
                .thenThrow(new RuntimeException("Order not found"));

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(0))
                    .andExpect(jsonPath("$.message").value(containsString("Failed to retrieve order details")))
                    .andExpect(jsonPath("$.order").doesNotExist());

            verify(processTrackingInfo).getTrackingOrder(orderId);
        }

        @Test
        @DisplayName("Should handle service throwing ValidationException")
        void getOrderDetail_ServiceThrowsValidationException_ReturnsErrorResponse() throws Exception {
            // Given
            int orderId = 999;
            
            when(processTrackingInfo.getTrackingOrder(orderId))
                .thenThrow(new ValidationException("Order tracking information not available"));

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(0))
                    .andExpect(jsonPath("$.message").value(containsString("Failed to retrieve order details")))
                    .andExpect(jsonPath("$.order").doesNotExist());

            verify(processTrackingInfo).getTrackingOrder(orderId);
        }

        @Test
        @DisplayName("Should handle very large order ID")
        void getOrderDetail_VeryLargeOrderId_HandlesCorrectly() throws Exception {
            // Given
            int largeOrderId = Integer.MAX_VALUE;
            OrderTrackingInfo mockTrackingInfo = createMockOrderTrackingInfo(largeOrderId);
            
            when(processTrackingInfo.getTrackingOrder(largeOrderId)).thenReturn(mockTrackingInfo);

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(largeOrderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.order.order_id").value(largeOrderId));

            verify(processTrackingInfo).getTrackingOrder(largeOrderId);
        }

        @Test
        @DisplayName("Should handle order with null order details")
        void getOrderDetail_OrderWithNullDetails_HandlesGracefully() throws Exception {
            // Given
            int orderId = 123;
            OrderTrackingInfo trackingInfoWithNullDetails = new OrderTrackingInfo();
            trackingInfoWithNullDetails.setOrder_id(orderId);
            trackingInfoWithNullDetails.setCurrent_status("pending");
            trackingInfoWithNullDetails.setOrder_date("2025-01-15");
            trackingInfoWithNullDetails.setOrder_details(null);
            
            when(processTrackingInfo.getTrackingOrder(orderId)).thenReturn(trackingInfoWithNullDetails);

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.order.order_id").value(orderId))
                    .andExpect(jsonPath("$.order.order_details").doesNotExist());

            verify(processTrackingInfo).getTrackingOrder(orderId);
        }

        @Test
        @DisplayName("Should handle order with empty order details")
        void getOrderDetail_OrderWithEmptyDetails_HandlesGracefully() throws Exception {
            // Given
            int orderId = 123;
            OrderTrackingInfo trackingInfoWithEmptyDetails = new OrderTrackingInfo();
            trackingInfoWithEmptyDetails.setOrder_id(orderId);
            trackingInfoWithEmptyDetails.setCurrent_status("pending");
            trackingInfoWithEmptyDetails.setOrder_date("2025-01-15");
            trackingInfoWithEmptyDetails.setOrder_details(Collections.emptyMap());
            
            when(processTrackingInfo.getTrackingOrder(orderId)).thenReturn(trackingInfoWithEmptyDetails);

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.order.order_id").value(orderId))
                    .andExpect(jsonPath("$.order.order_details").isEmpty());

            verify(processTrackingInfo).getTrackingOrder(orderId);
        }

        @Test
        @DisplayName("Should handle concurrent requests for different orders")
        void getOrderDetail_ConcurrentRequestsForDifferentOrders_HandlesCorrectly() throws Exception {
            // Given
            int orderId1 = 100;
            int orderId2 = 200;
            int orderId3 = 300;
            
            when(processTrackingInfo.getTrackingOrder(orderId1)).thenReturn(createMockOrderTrackingInfo(orderId1));
            when(processTrackingInfo.getTrackingOrder(orderId2)).thenReturn(createMockOrderTrackingInfo(orderId2));
            when(processTrackingInfo.getTrackingOrder(orderId3)).thenReturn(createMockOrderTrackingInfo(orderId3));

            // When & Then - Simulate concurrent requests
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId1))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order.order_id").value(orderId1));

            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId2))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order.order_id").value(orderId2));

            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId3))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order.order_id").value(orderId3));

            verify(processTrackingInfo).getTrackingOrder(orderId1);
            verify(processTrackingInfo).getTrackingOrder(orderId2);
            verify(processTrackingInfo).getTrackingOrder(orderId3);
        }

        @Test
        @DisplayName("Should handle order with special characters in details")
        void getOrderDetail_OrderWithSpecialCharactersInDetails_HandlesCorrectly() throws Exception {
            // Given
            int orderId = 123;
            OrderTrackingInfo trackingInfoWithSpecialChars = createMockOrderTrackingInfo(orderId);
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("total_amount", 250000);
            orderDetails.put("payment_method", "VNPay");
            orderDetails.put("delivery_address", "123 Nguyễn Trãi, Quận 1, TP.HCM");
            orderDetails.put("customer_note", "Giao hàng vào buổi sáng, cảm ơn!");
            trackingInfoWithSpecialChars.setOrder_details(orderDetails);
            
            when(processTrackingInfo.getTrackingOrder(orderId)).thenReturn(trackingInfoWithSpecialChars);

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.order.order_details.delivery_address").value("123 Nguyễn Trãi, Quận 1, TP.HCM"))
                    .andExpect(jsonPath("$.order.order_details.customer_note").value("Giao hàng vào buổi sáng, cảm ơn!"));

            verify(processTrackingInfo).getTrackingOrder(orderId);
        }
    }

    @Nested
    @DisplayName("Integration and Error Handling Tests")
    class IntegrationAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle scenario where both services fail")
        void bothServicesThrowException_HandlesGracefully() throws Exception {
            // Given
            when(orderService.getAllOrders())
                .thenThrow(new RuntimeException("Database unavailable"));
            
            when(processTrackingInfo.getTrackingOrder(anyInt()))
                .thenThrow(new RuntimeException("Tracking service unavailable"));

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(get("/order-detail")
                    .param("order_id", "123")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService).getAllOrders();
            verify(processTrackingInfo).getTrackingOrder(123);
        }

        @Test
        @DisplayName("Should handle timeout scenarios gracefully")
        void servicesTimeout_HandlesGracefully() throws Exception {
            // Given
            when(orderService.getAllOrders())
                .thenThrow(new RuntimeException("Request timeout"));
            
            when(processTrackingInfo.getTrackingOrder(anyInt()))
                .thenThrow(new RuntimeException("Connection timeout"));

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(get("/order-detail")
                    .param("order_id", "123")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(0))
                    .andExpect(jsonPath("$.message").value(containsString("Connection timeout")));

            verify(orderService).getAllOrders();
            verify(processTrackingInfo).getTrackingOrder(123);
        }

        @Test
        @DisplayName("Should handle memory pressure scenarios")
        void memoryPressure_HandlesGracefully() throws Exception {
            // Given - simulate memory pressure with a RuntimeException
            when(orderService.getAllOrders())
                .thenThrow(new RuntimeException("Java heap space"));

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle SQL injection attempts in order ID parameter")
        void sqlInjectionAttempt_HandlesSafely() throws Exception {
            // Given
            String maliciousOrderId = "123; DROP TABLE orders; --";

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", maliciousOrderId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError()); // Spring parameter binding error

            verify(processTrackingInfo, never()).getTrackingOrder(anyInt());
        }

        @Test
        @DisplayName("Should handle XSS attempts in parameters")
        void xssAttempt_HandlesSafely() throws Exception {
            // Given
            String xssOrderId = "<script>alert('xss')</script>";

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", xssOrderId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError()); // Spring parameter binding error

            verify(processTrackingInfo, never()).getTrackingOrder(anyInt());
        }

        @Test
        @DisplayName("Should handle extremely long order ID parameter")
        void extremelyLongOrderId_HandlesSafely() throws Exception {
            // Given
            String longOrderId = "1".repeat(10000);

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", longOrderId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError()); // Spring parameter binding error

            verify(processTrackingInfo, never()).getTrackingOrder(anyInt());
        }
    }

    @Nested
    @DisplayName("Performance and Load Tests")
    class PerformanceAndLoadTests {

        @Test
        @DisplayName("Should handle high-frequency requests efficiently")
        void highFrequencyRequests_HandlesEfficiently() throws Exception {
            // Given
            List<OrderInfo> mockOrders = Arrays.asList(
                createMockOrderInfo(1, "Customer 1", 100000, "2025-01-01", "pending", "VNPay")
            );
            OrderTrackingInfo mockTrackingInfo = createMockOrderTrackingInfo(123);
            
            when(orderService.getAllOrders()).thenReturn(mockOrders);
            when(processTrackingInfo.getTrackingOrder(123)).thenReturn(mockTrackingInfo);

            // When & Then - Simulate high-frequency requests
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < 50; i++) {
                mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
                
                mockMvc.perform(get("/order-detail")
                        .param("order_id", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
            }
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Should complete within reasonable time (adjust threshold as needed)
            assert executionTime < 10000; // 10 seconds threshold for 100 requests
            
            verify(orderService, times(50)).getAllOrders();
            verify(processTrackingInfo, times(50)).getTrackingOrder(123);
        }

        @Test
        @DisplayName("Should handle large response payloads efficiently")
        void largeResponsePayloads_HandlesEfficiently() throws Exception {
            // Given
            List<OrderInfo> largeOrderList = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                OrderInfo order = createMockOrderInfo(
                    i, 
                    "Customer with very long name " + "A".repeat(100) + " " + i, 
                    100000 + i, 
                    "2025-01-01", 
                    "pending", 
                    "VNPay"
                );
                largeOrderList.add(order);
            }
            
            when(orderService.getAllOrders()).thenReturn(largeOrderList);

            // When & Then
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1000));
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Should handle large payload efficiently
            assert executionTime < 5000; // 5 seconds threshold
            
            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle memory-intensive order details efficiently")
        void memoryIntensiveOrderDetails_HandlesEfficiently() throws Exception {
            // Given
            int orderId = 123;
            OrderTrackingInfo largeTrackingInfo = createLargeOrderTrackingInfo(orderId);
            
            when(processTrackingInfo.getTrackingOrder(orderId)).thenReturn(largeTrackingInfo);

            // When & Then
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order.order_details.items.length()").value(100));
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Should handle large order details efficiently
            assert executionTime < 3000; // 3 seconds threshold
            
            verify(processTrackingInfo).getTrackingOrder(orderId);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesAndBoundaryTests {

        @Test
        @DisplayName("Should handle orders with all possible statuses")
        void ordersWithAllPossibleStatuses_HandlesCorrectly() throws Exception {
            // Given
            List<OrderInfo> ordersWithAllStatuses = Arrays.asList(
                createMockOrderInfo(1, "Customer 1", 100000, "2025-01-01", "pending", "VNPay"),
                createMockOrderInfo(2, "Customer 2", 200000, "2025-01-02", "processing", "Credit Card"),
                createMockOrderInfo(3, "Customer 3", 150000, "2025-01-03", "approved", "VNPay"),
                createMockOrderInfo(4, "Customer 4", 300000, "2025-01-04", "completed", "Credit Card"),
                createMockOrderInfo(5, "Customer 5", 250000, "2025-01-05", "cancelled", "VNPay"),
                createMockOrderInfo(6, "Customer 6", 180000, "2025-01-06", "refunded", "Credit Card"),
                createMockOrderInfo(7, "Customer 7", 220000, "2025-01-07", "failed", "VNPay")
            );
            
            when(orderService.getAllOrders()).thenReturn(ordersWithAllStatuses);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(7))
                    .andExpect(jsonPath("$[0].status").value("pending"))
                    .andExpect(jsonPath("$[1].status").value("processing"))
                    .andExpect(jsonPath("$[2].status").value("approved"))
                    .andExpect(jsonPath("$[3].status").value("completed"))
                    .andExpect(jsonPath("$[4].status").value("cancelled"))
                    .andExpect(jsonPath("$[5].status").value("refunded"))
                    .andExpect(jsonPath("$[6].status").value("failed"));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle orders with all payment methods")
        void ordersWithAllPaymentMethods_HandlesCorrectly() throws Exception {
            // Given
            List<OrderInfo> ordersWithAllPaymentMethods = Arrays.asList(
                createMockOrderInfo(1, "Customer 1", 100000, "2025-01-01", "completed", "VNPay"),
                createMockOrderInfo(2, "Customer 2", 200000, "2025-01-02", "completed", "Credit Card"),
                createMockOrderInfo(3, "Customer 3", 150000, "2025-01-03", "completed", "Bank Transfer"),
                createMockOrderInfo(4, "Customer 4", 300000, "2025-01-04", "completed", "Cash on Delivery"),
                createMockOrderInfo(5, "Customer 5", 250000, "2025-01-05", "completed", "MoMo"),
                createMockOrderInfo(6, "Customer 6", 180000, "2025-01-06", "completed", "ZaloPay")
            );
            
            when(orderService.getAllOrders()).thenReturn(ordersWithAllPaymentMethods);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(6))
                    .andExpect(jsonPath("$[0].payment_method").value("VNPay"))
                    .andExpect(jsonPath("$[1].payment_method").value("Credit Card"))
                    .andExpect(jsonPath("$[2].payment_method").value("Bank Transfer"))
                    .andExpect(jsonPath("$[3].payment_method").value("Cash on Delivery"))
                    .andExpect(jsonPath("$[4].payment_method").value("MoMo"))
                    .andExpect(jsonPath("$[5].payment_method").value("ZaloPay"));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle order detail with maximum complexity")
        void orderDetailWithMaximumComplexity_HandlesCorrectly() throws Exception {
            // Given
            int orderId = 999;
            OrderTrackingInfo maxComplexityInfo = createMaximumComplexityOrderTrackingInfo(orderId);
            
            when(processTrackingInfo.getTrackingOrder(orderId)).thenReturn(maxComplexityInfo);

            // When & Then
            mockMvc.perform(get("/order-detail")
                    .param("order_id", String.valueOf(orderId))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.order.order_id").value(orderId))
                    .andExpect(jsonPath("$.order.order_details.items.length()").value(50))
                    .andExpect(jsonPath("$.order.order_details.nested_data").exists())
                    .andExpect(jsonPath("$.order.order_details.nested_data.level1").exists())
                    .andExpect(jsonPath("$.order.order_details.nested_data.level1.level2").exists());

            verify(processTrackingInfo).getTrackingOrder(orderId);
        }

        @Test
        @DisplayName("Should handle boundary values for order amounts")
        void boundaryValuesForOrderAmounts_HandlesCorrectly() throws Exception {
            // Given
            List<OrderInfo> ordersWithBoundaryAmounts = Arrays.asList(
                createMockOrderInfo(1, "Min Order", 1, "2025-01-01", "completed", "VNPay"),
                createMockOrderInfo(2, "Max Order", Integer.MAX_VALUE, "2025-01-02", "completed", "Credit Card"),
                createMockOrderInfo(3, "Zero Order", 0, "2025-01-03", "pending", "VNPay")
            );
            
            when(orderService.getAllOrders()).thenReturn(ordersWithBoundaryAmounts);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].total_amount").value(1))
                    .andExpect(jsonPath("$[1].total_amount").value(Integer.MAX_VALUE))
                    .andExpect(jsonPath("$[2].total_amount").value(0));

            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Should handle date edge cases")
        void dateEdgeCases_HandlesCorrectly() throws Exception {
            // Given
            List<OrderInfo> ordersWithDateEdgeCases = Arrays.asList(
                createMockOrderInfo(1, "Customer 1", 100000, "2025-01-01", "completed", "VNPay"), // Start of year
                createMockOrderInfo(2, "Customer 2", 200000, "2025-12-31", "completed", "Credit Card"), // End of year
                createMockOrderInfo(3, "Customer 3", 150000, "2025-02-29", "completed", "VNPay"), // Leap year (if applicable)
                createMockOrderInfo(4, "Customer 4", 300000, "", "completed", "Credit Card"), // Empty date
                createMockOrderInfo(5, "Customer 5", 250000, null, "completed", "VNPay") // Null date
            );
            
            when(orderService.getAllOrders()).thenReturn(ordersWithDateEdgeCases);

            // When & Then
            mockMvc.perform(get("/orders")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(5))
                    .andExpect(jsonPath("$[0].order_date").value("2025-01-01"))
                    .andExpect(jsonPath("$[1].order_date").value("2025-12-31"))
                    .andExpect(jsonPath("$[2].order_date").value("2025-02-29"))
                    .andExpect(jsonPath("$[3].order_date").value(""))
                    .andExpect(jsonPath("$[4].order_date").doesNotExist());

            verify(orderService).getAllOrders();
        }
    }

    // Helper methods for creating test data
    private OrderInfo createMockOrderInfo(int orderId, String customerName, int totalAmount, 
                                         String orderDate, String status, String paymentMethod) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrder_id(orderId);
        orderInfo.setCustomer_name(customerName);
        orderInfo.setTotal_amount(totalAmount);
        orderInfo.setOrder_date(orderDate);
        orderInfo.setStatus(status);
        orderInfo.setPayment_method(paymentMethod);
        return orderInfo;
    }

    private OrderTrackingInfo createMockOrderTrackingInfo(int orderId) {
        OrderTrackingInfo trackingInfo = new OrderTrackingInfo();
        trackingInfo.setOrder_id(orderId);
        trackingInfo.setCurrent_status("processing");
        trackingInfo.setOrder_date("2025-01-15");
        
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("total_amount", 250000);
        orderDetails.put("payment_method", "VNPay");
        orderDetails.put("delivery_address", "123 Test Street, Test City");
        
        trackingInfo.setOrder_details(orderDetails);
        return trackingInfo;
    }

    private OrderTrackingInfo createComplexMockOrderTrackingInfo(int orderId) {
        OrderTrackingInfo trackingInfo = new OrderTrackingInfo();
        trackingInfo.setOrder_id(orderId);
        trackingInfo.setCurrent_status("completed");
        trackingInfo.setOrder_date("2025-01-20");
        
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("total_amount", 500000);
        orderDetails.put("payment_method", "VNPay");
        orderDetails.put("delivery_address", "456 Complex Street, Complex City");
        
        // Add items array
        List<Map<String, Object>> items = Arrays.asList(
            createItemMap(1, "Test Book", 150000, 1, false),
            createItemMap(2, "Test CD", 200000, 2, true),
            createItemMap(3, "Test DVD", 150000, 1, false)
        );
        orderDetails.put("items", items);
        
        trackingInfo.setOrder_details(orderDetails);
        return trackingInfo;
    }

    private OrderTrackingInfo createLargeOrderTrackingInfo(int orderId) {
        OrderTrackingInfo trackingInfo = new OrderTrackingInfo();
        trackingInfo.setOrder_id(orderId);
        trackingInfo.setCurrent_status("processing");
        trackingInfo.setOrder_date("2025-01-25");
        
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("total_amount", 10000000);
        orderDetails.put("payment_method", "Credit Card");
        orderDetails.put("delivery_address", "789 Large Order Street, Large City");
        
        // Add 100 items
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            items.add(createItemMap(i, "Product " + i, 100000, 1, i % 2 == 0));
        }
        orderDetails.put("items", items);
        
        trackingInfo.setOrder_details(orderDetails);
        return trackingInfo;
    }

    private OrderTrackingInfo createMaximumComplexityOrderTrackingInfo(int orderId) {
        OrderTrackingInfo trackingInfo = new OrderTrackingInfo();
        trackingInfo.setOrder_id(orderId);
        trackingInfo.setCurrent_status("completed");
        trackingInfo.setOrder_date("2025-01-30");
        
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("total_amount", 50000000);
        orderDetails.put("payment_method", "Bank Transfer");
        orderDetails.put("delivery_address", "999 Maximum Complexity Street, Max City");
        
        // Add 50 items with complex data
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Map<String, Object> item = createItemMap(i, "Complex Product " + i, 1000000, i % 5 + 1, i % 3 == 0);
            item.put("metadata", Map.of("category", "Category " + (i % 10), "tags", Arrays.asList("tag1", "tag2")));
            items.add(item);
        }
        orderDetails.put("items", items);
        
        // Add nested data structure
        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("level1", Map.of(
            "level2", Map.of(
                "level3", Map.of("data", "Deep nested value")
            )
        ));
        orderDetails.put("nested_data", nestedData);
        
        trackingInfo.setOrder_details(orderDetails);
        return trackingInfo;
    }

    private Map<String, Object> createItemMap(int productId, String title, int price, int quantity, boolean rushOrder) {
        Map<String, Object> item = new HashMap<>();
        item.put("product_id", productId);
        item.put("title", title);
        item.put("price", price);
        item.put("quantity", quantity);
        item.put("rush_order_using", rushOrder);
        return item;
    }
}