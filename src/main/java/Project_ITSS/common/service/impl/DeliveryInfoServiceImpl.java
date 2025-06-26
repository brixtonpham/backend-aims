package Project_ITSS.common.service.impl;

import Project_ITSS.entity.DeliveryInformation;
import Project_ITSS.common.repository.IDeliveryInfoRepository;
import Project_ITSS.common.service.IDeliveryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryInfoServiceImpl implements IDeliveryInfoService {

    private final IDeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    public DeliveryInfoServiceImpl(IDeliveryInfoRepository deliveryInfoRepository) {
        this.deliveryInfoRepository = deliveryInfoRepository;
    }

    @Override
    public int saveDeliveryInfo(DeliveryInformation deliveryInfo) {
        return deliveryInfoRepository.saveDeliveryInfo(deliveryInfo);
    }

    @Override
    public Optional<DeliveryInformation> getDeliveryInfoById(int deliveryId) {
        return deliveryInfoRepository.findById(deliveryId);
    }

    @Override
    public List<DeliveryInformation> getAllDeliveryInfos() {
        return deliveryInfoRepository.findAll();
    }

    @Override
    public void updateDeliveryInfo(DeliveryInformation deliveryInfo) {
        deliveryInfoRepository.updateDeliveryInfo(deliveryInfo);
    }

    @Override
    public void deleteDeliveryInfo(int deliveryId) {
        deliveryInfoRepository.deleteById(deliveryId);
    }
}