package Project_ITSS.service;

import Project_ITSS.entity.DeliveryInformation;
import Project_ITSS.common.service.IDeliveryInfoService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.repository.DeliveryInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryInfoService implements IDeliveryInfoService {
    
    private final DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    public DeliveryInfoService(DeliveryInfoRepository deliveryInfoRepository) {
        this.deliveryInfoRepository = deliveryInfoRepository;
    }

    @Override
    public int saveDeliveryInfo(DeliveryInformation deliveryInfo) {
        if (deliveryInfo == null) {
            throw new ValidationException("Delivery information cannot be null");
        }
        return deliveryInfoRepository.saveDeliveryInfo(deliveryInfo);
    }

    @Override
    public Optional<DeliveryInformation> getDeliveryInfoById(int deliveryId) {
        return deliveryInfoRepository.getDeliveryInfoById(deliveryId);
    }

    @Override
    public List<DeliveryInformation> getAllDeliveryInfos() {
        return deliveryInfoRepository.getAllDeliveryInfos();
    }

    @Override
    public void updateDeliveryInfo(DeliveryInformation deliveryInfo) {
        if (deliveryInfo == null) {
            throw new ValidationException("Delivery information cannot be null");
        }
        deliveryInfoRepository.updateDeliveryInfo(deliveryInfo);
    }

    @Override
    public void deleteDeliveryInfo(int deliveryId) {
        deliveryInfoRepository.deleteDeliveryInfo(deliveryId);
    }
}