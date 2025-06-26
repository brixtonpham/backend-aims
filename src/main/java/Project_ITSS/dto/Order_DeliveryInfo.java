package Project_ITSS.dto;

import Project_ITSS.entity.DeliveryInformation;
import Project_ITSS.entity.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order_DeliveryInfo {
    private Order order;
    private DeliveryInformation deliveryInformation;
}
