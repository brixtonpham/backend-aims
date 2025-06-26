package Project_ITSS.dto;

import Project_ITSS.entity.DeliveryInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfoResponse {
    private DeliveryInformation delivery_information;
} 