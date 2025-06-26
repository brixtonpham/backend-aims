package Project_ITSS.service;

import Project_ITSS.entity.DeliveryInformation;
import Project_ITSS.repository.DeliveryInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryInformationService {

    @Autowired
    private DeliveryInfoRepository deliveryInformationRepo;
    public int saveDeliveryInfo(DeliveryInformation deliveryInformation) {
        return deliveryInformationRepo.saveDeliveryInfo(deliveryInformation);
    }
} 