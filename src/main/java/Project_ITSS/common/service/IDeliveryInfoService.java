package Project_ITSS.common.service;

import Project_ITSS.entity.DeliveryInformation;
import java.util.List;
import java.util.Optional;

public interface IDeliveryInfoService {
    
    int saveDeliveryInfo(DeliveryInformation deliveryInfo);
    
    Optional<DeliveryInformation> getDeliveryInfoById(int deliveryId);
    
    List<DeliveryInformation> getAllDeliveryInfos();
    
    void updateDeliveryInfo(DeliveryInformation deliveryInfo);
    
    void deleteDeliveryInfo(int deliveryId);
}