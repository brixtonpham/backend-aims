package Project_ITSS.common.repository;

import Project_ITSS.entity.DeliveryInformation;
import java.util.List;
import java.util.Optional;

public interface IDeliveryInfoRepository {
    
    int saveDeliveryInfo(DeliveryInformation deliveryInfo);
    
    Optional<DeliveryInformation> findById(int deliveryId);
    
    List<DeliveryInformation> findAll();
    
    void updateDeliveryInfo(DeliveryInformation deliveryInfo);
    
    void deleteById(int deliveryId);
    
    boolean existsById(int deliveryId);
    
    List<DeliveryInformation> findByProvince(String province);
}