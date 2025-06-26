package Project_ITSS.controller;

import Project_ITSS.dto.*;
import Project_ITSS.entity.*;
import Project_ITSS.common.entity.Product;
import Project_ITSS.exception.PlaceOrderException;
import Project_ITSS.common.service.OrderBusinessService;
import Project_ITSS.common.validation.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaceOrderController Unit Tests")
class PlaceOrderControllerTest {

    @Mock
    private OrderBusinessService orderBusinessService;

    @InjectMocks
    private PlaceOrderController placeOrderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(placeOrderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // ==================== TEST ENDPOINT ==================== //

    @Test
    @DisplayName("Should return test successful message")
    void testEndpoint_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test successful"));
    }

    // ==================== PLACE ORDER TESTS ==================== //

    @Test
    @DisplayName("Should place order successfully with valid cart")
    void requestToPlaceOrder_WithValidCart_ShouldReturnSuccess() throws Exception {
        // Given
        Cart cart = createValidCart();
        Order expectedOrder = createValidOrder();
        
        when(orderBusinessService.validateAndCreateOrder(any(Cart.class)))
                .thenReturn(expectedOrder);

        // When & Then
        mockMvc.perform(post("/placeorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.order_id", is(expectedOrder.getOrder_id())))
                .andExpect(jsonPath("$.order.Total_before_VAT", is(expectedOrder.getTotal_before_VAT())))
                .andExpect(jsonPath("$.order.Total_after_VAT", is(expectedOrder.getTotal_after_VAT())))
                .andExpect(jsonPath("$.order.status", is(expectedOrder.getStatus())))
                .andExpect(jsonPath("$.message", is("Successfully")));

        verify(orderBusinessService).validateAndCreateOrder(any(Cart.class));
    }

    @Test
    @DisplayName("Should handle null cart with validation exception")
    void requestToPlaceOrder_WithNullCart_ShouldThrowValidationException() throws Exception {
        mockMvc.perform(post("/placeorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cart cannot be null"));

        verify(orderBusinessService, never()).validateAndCreateOrder(any());
    }

    @Test
    @DisplayName("Should handle empty cart")
    void requestToPlaceOrder_WithEmptyCart_ShouldProcessSuccessfully() throws Exception {
        // Given
        Cart emptyCart = new Cart();
        Order expectedOrder = new Order();
        expectedOrder.setOrder_id(1);
        expectedOrder.setStatus("pending");
        
        when(orderBusinessService.validateAndCreateOrder(any(Cart.class)))
                .thenReturn(expectedOrder);

        // When & Then
        mockMvc.perform(post("/placeorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyCart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.order_id", is(1)))
                .andExpect(jsonPath("$.order.status", is("pending")))
                .andExpect(jsonPath("$.message", is("Successfully")));

        verify(orderBusinessService).validateAndCreateOrder(any(Cart.class));
    }

    @Test
    @DisplayName("Should handle PlaceOrderException during order creation")
    void requestToPlaceOrder_WithPlaceOrderException_ShouldReturnBadRequest() throws Exception {
        // Given
        Cart cart = createValidCart();
        String errorMessage = "Inadequate product quantity";
        
        when(orderBusinessService.validateAndCreateOrder(any(Cart.class)))
                .thenThrow(new PlaceOrderException(errorMessage));

        // When & Then
        mockMvc.perform(post("/placeorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.order").doesNotExist())
                .andExpect(jsonPath("$.message", is(errorMessage)));

        verify(orderBusinessService).validateAndCreateOrder(any(Cart.class));
    }

    @Test
    @DisplayName("Should handle invalid JSON format")
    void requestToPlaceOrder_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/placeorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(orderBusinessService, never()).validateAndCreateOrder(any());
    }

    // ==================== DELIVERY INFO TESTS ==================== //

    @Test
    @DisplayName("Should submit delivery information successfully")
    void submitDeliveryInformation_WithValidInfo_ShouldReturnSuccess() throws Exception {
        // Given
        DeliveryInformation deliveryInfo = createValidDeliveryInfo();
        DeliveryInformation validatedInfo = createValidDeliveryInfo();
        validatedInfo.setDelivery_id(1);
        
        when(orderBusinessService.validateAndCreateDeliveryInfo(any(DeliveryInformation.class)))
                .thenReturn(validatedInfo);

        // When & Then
        mockMvc.perform(post("/deliveryinfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.delivery_information.delivery_id", is(1)))
                .andExpect(jsonPath("$.delivery_information.name", is(validatedInfo.getName())))
                .andExpect(jsonPath("$.delivery_information.email", is(validatedInfo.getEmail())))
                .andExpect(jsonPath("$.delivery_information.phone", is(validatedInfo.getPhone())))
                .andExpect(jsonPath("$.delivery_information.address", is(validatedInfo.getAddress())));

        verify(orderBusinessService).validateAndCreateDeliveryInfo(any(DeliveryInformation.class));
    }

    @Test
    @DisplayName("Should handle null delivery information")
    void submitDeliveryInformation_WithNullInfo_ShouldThrowValidationException() throws Exception {
        mockMvc.perform(post("/deliveryinfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Delivery information cannot be null"));

        verify(orderBusinessService, never()).validateAndCreateDeliveryInfo(any());
    }

    @Test
    @DisplayName("Should handle validation exception from delivery info service")
    void submitDeliveryInformation_WithInvalidInfo_ShouldReturnBadRequest() throws Exception {
        // Given
        DeliveryInformation invalidInfo = createValidDeliveryInfo();
        invalidInfo.setEmail("invalid-email");
        String errorMessage = "Invalid email format";
        
        when(orderBusinessService.validateAndCreateDeliveryInfo(any(DeliveryInformation.class)))
                .thenThrow(new ValidationException(errorMessage));

        // When & Then
        mockMvc.perform(post("/deliveryinfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidInfo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(orderBusinessService).validateAndCreateDeliveryInfo(any(DeliveryInformation.class));
    }

    // ==================== RECALCULATE TESTS ==================== //

    @Test
    @DisplayName("Should recalculate delivery fee successfully")
    void recalculateDeliveryFee_WithValidData_ShouldReturnFees() throws Exception {
        // Given
        CalculateFeeDTO feeDTO = createValidCalculateFeeDTO();
        int[] fees = {25000, 35000}; // [regular, rush]
        
        when(orderBusinessService.calculateDeliveryFee(any(CalculateFeeDTO.class)))
                .thenReturn(fees);

        // When & Then
        mockMvc.perform(post("/recalculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regularShipping", is(25000)))
                .andExpect(jsonPath("$.rushShipping", is(35000)))
                .andExpect(jsonPath("$.totalShipping", is(60000)));

        verify(orderBusinessService).calculateDeliveryFee(any(CalculateFeeDTO.class));
    }

    @Test
    @DisplayName("Should handle null fee calculation data")
    void recalculateDeliveryFee_WithNullData_ShouldThrowValidationException() throws Exception {
        mockMvc.perform(post("/recalculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Fee information cannot be null"));

        verify(orderBusinessService, never()).calculateDeliveryFee(any());
    }

    @Test
    @DisplayName("Should handle exception during fee calculation")
    void recalculateDeliveryFee_WithException_ShouldReturnBadRequest() throws Exception {
        // Given
        CalculateFeeDTO feeDTO = createValidCalculateFeeDTO();
        String errorMessage = "Failed to calculate delivery fee";
        
        when(orderBusinessService.calculateDeliveryFee(any(CalculateFeeDTO.class)))
                .thenThrow(new PlaceOrderException(errorMessage));

        // When & Then
        mockMvc.perform(post("/recalculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(orderBusinessService).calculateDeliveryFee(any(CalculateFeeDTO.class));
    }

    @Test
    @DisplayName("Should handle zero delivery fees")
    void recalculateDeliveryFee_WithZeroFees_ShouldReturnZeros() throws Exception {
        // Given
        CalculateFeeDTO feeDTO = createValidCalculateFeeDTO();
        int[] fees = {0, 0};
        
        when(orderBusinessService.calculateDeliveryFee(any(CalculateFeeDTO.class)))
                .thenReturn(fees);

        // When & Then
        mockMvc.perform(post("/recalculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regularShipping", is(0)))
                .andExpect(jsonPath("$.rushShipping", is(0)))
                .andExpect(jsonPath("$.totalShipping", is(0)));

        verify(orderBusinessService).calculateDeliveryFee(any(CalculateFeeDTO.class));
    }

    // ==================== FINISH ORDER TESTS ==================== //

    @Test
    @DisplayName("Should finish place order successfully")
    void finishPlaceOrder_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        Order_DeliveryInfo orderInfoDTO = createValidOrderDeliveryInfo();
        
        doNothing().when(orderBusinessService).completeOrderPlacement(any(Order_DeliveryInfo.class));

        // When & Then
        mockMvc.perform(post("/finish-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderInfoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)));

        verify(orderBusinessService).completeOrderPlacement(any(Order_DeliveryInfo.class));
    }

    @Test
    @DisplayName("Should handle null order information")
    void finishPlaceOrder_WithNullData_ShouldThrowValidationException() throws Exception {
        mockMvc.perform(post("/finish-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Order information cannot be null"));

        verify(orderBusinessService, never()).completeOrderPlacement(any());
    }

    @Test
    @DisplayName("Should handle exception during order completion")
    void finishPlaceOrder_WithException_ShouldReturnBadRequest() throws Exception {
        // Given
        Order_DeliveryInfo orderInfoDTO = createValidOrderDeliveryInfo();
        String errorMessage = "Failed to complete order";
        
        doThrow(new PlaceOrderException(errorMessage))
                .when(orderBusinessService).completeOrderPlacement(any(Order_DeliveryInfo.class));

        // When & Then
        mockMvc.perform(post("/finish-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderInfoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(orderBusinessService).completeOrderPlacement(any(Order_DeliveryInfo.class));
    }

    // ==================== HELPER METHODS ==================== //

    private Cart createValidCart() {
        Cart cart = new Cart();
        
        // Create cart items
        CartItem item1 = new CartItem();
        item1.setQuantity(2);
        item1.setProduct(createValidProduct(1L, "Book 1", 100000));
        
        CartItem item2 = new CartItem();
        item2.setQuantity(1);
        item2.setProduct(createValidProduct(2L, "CD 1", 50000));
        
        cart.addProducts(Arrays.asList(item1, item2));
        
        return cart;
    }

    private Product createValidProduct(Long id, String title, int price) {
        Product product = new Product();
        product.setProductId(id);
        product.setTitle(title);
        product.setPrice(price);
        product.setWeight(0.5f);
        product.setRushOrderSupported(true);
        product.setQuantity(10);
        product.setType("book");
        return product;
    }

    private Order createValidOrder() {
        Order order = new Order();
        order.setOrder_id(12345);
        order.setTotal_before_VAT(150000);
        order.setTotal_after_VAT(165000);
        order.setStatus("pending");
        order.setPayment_method("VNPay");
        return order;
    }

    private DeliveryInformation createValidDeliveryInfo() {
        DeliveryInformation info = new DeliveryInformation();
        info.setName("Nguyen Van A");
        info.setPhone("0987654321");
        info.setEmail("test@example.com");
        info.setAddress("123 Test Street");
        info.setProvince("Hanoi");
        info.setDelivery_message("Please call before delivery");
        info.setDelivery_fee(25000);
        return info;
    }

    private CalculateFeeDTO createValidCalculateFeeDTO() {
        CalculateFeeDTO feeDTO = new CalculateFeeDTO();
        feeDTO.setProvince("Hanoi");
        feeDTO.setOrder(createValidOrder());
        return feeDTO;
    }

    private Order_DeliveryInfo createValidOrderDeliveryInfo() {
        Order_DeliveryInfo orderInfoDTO = new Order_DeliveryInfo();
        orderInfoDTO.setOrder(createValidOrder());
        orderInfoDTO.setDeliveryInformation(createValidDeliveryInfo());
        return orderInfoDTO;
    }

    // ==================== INTEGRATION TESTS ==================== //

    @Test
    @DisplayName("Should handle complete order flow successfully")
    void completeOrderFlow_ShouldProcessAllStepsSuccessfully() throws Exception {
        // Given
        Cart cart = createValidCart();
        Order order = createValidOrder();
        DeliveryInformation deliveryInfo = createValidDeliveryInfo();
        CalculateFeeDTO feeDTO = createValidCalculateFeeDTO();
        Order_DeliveryInfo orderDeliveryInfo = createValidOrderDeliveryInfo();
        
        when(orderBusinessService.validateAndCreateOrder(any(Cart.class))).thenReturn(order);
        when(orderBusinessService.validateAndCreateDeliveryInfo(any(DeliveryInformation.class))).thenReturn(deliveryInfo);
        when(orderBusinessService.calculateDeliveryFee(any(CalculateFeeDTO.class))).thenReturn(new int[]{25000, 35000});
        doNothing().when(orderBusinessService).completeOrderPlacement(any(Order_DeliveryInfo.class));

        // Test step 1: Place order
        mockMvc.perform(post("/placeorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Successfully")));

        // Test step 2: Submit delivery info
        mockMvc.perform(post("/deliveryinfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryInfo)))
                .andExpect(status().isOk());

        // Test step 3: Recalculate fees
        mockMvc.perform(post("/recalculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalShipping", is(60000)));

        // Test step 4: Finish order
        mockMvc.perform(post("/finish-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDeliveryInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)));

        // Verify all interactions
        verify(orderBusinessService).validateAndCreateOrder(any(Cart.class));
        verify(orderBusinessService).validateAndCreateDeliveryInfo(any(DeliveryInformation.class));
        verify(orderBusinessService).calculateDeliveryFee(any(CalculateFeeDTO.class));
        verify(orderBusinessService).completeOrderPlacement(any(Order_DeliveryInfo.class));
    }

    @Test
    @DisplayName("Should handle multiple validation errors appropriately")
    void multipleValidationErrors_ShouldReturnAppropriateErrorMessages() throws Exception {
        // Test multiple scenarios with different validation errors
        
        // Scenario 1: Null cart
        mockMvc.perform(post("/placeorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cart cannot be null"));

        // Scenario 2: Null delivery info
        mockMvc.perform(post("/deliveryinfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Delivery information cannot be null"));

        // Scenario 3: Null fee info
        mockMvc.perform(post("/recalculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Fee information cannot be null"));

        // Scenario 4: Null order info
        mockMvc.perform(post("/finish-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Order information cannot be null"));

        // Verify no service interactions for null inputs
        verify(orderBusinessService, never()).validateAndCreateOrder(any());
        verify(orderBusinessService, never()).validateAndCreateDeliveryInfo(any());
        verify(orderBusinessService, never()).calculateDeliveryFee(any());
        verify(orderBusinessService, never()).completeOrderPlacement(any());
    }

    @Test
    @DisplayName("Should handle concurrent requests appropriately")
    void concurrentRequests_ShouldHandleAppropriately() throws Exception {
        // Given
        Cart cart = createValidCart();
        Order order = createValidOrder();
        
        when(orderBusinessService.validateAndCreateOrder(any(Cart.class))).thenReturn(order);

        // When - Simulate multiple concurrent requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/placeorder")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cart)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("Successfully")));
        }

        // Then - Verify service was called 5 times
        verify(orderBusinessService, times(5)).validateAndCreateOrder(any(Cart.class));
    }
}