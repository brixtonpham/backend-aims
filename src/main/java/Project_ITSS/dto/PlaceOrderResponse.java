package Project_ITSS.dto;

import Project_ITSS.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderResponse {
    private Order order;
    private String message;
} 