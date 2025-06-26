package Project_ITSS.common.service.impl;

import Project_ITSS.entity.Order;
import Project_ITSS.entity.Orderline;
import Project_ITSS.common.repository.IOrderlineRepository;
import Project_ITSS.common.service.IOrderlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderlineServiceImpl implements IOrderlineService {

    private final IOrderlineRepository orderlineRepository;

    @Autowired
    public OrderlineServiceImpl(IOrderlineRepository orderlineRepository) {
        this.orderlineRepository = orderlineRepository;
    }

    @Override
    public void saveOrderlines(Order order) {
        orderlineRepository.saveOrderlines(order);
    }

    @Override
    public void saveOrderline(Orderline orderline) {
        orderlineRepository.saveOrderline(orderline);
    }

    @Override
    public List<Orderline> getOrderlinesByOrderId(long orderId) {
        return orderlineRepository.findByOrderId(orderId);
    }

    @Override
    public void updateOrderline(Orderline orderline) {
        orderlineRepository.updateOrderline(orderline);
    }

    @Override
    public void deleteOrderlinesByOrderId(long orderId) {
        orderlineRepository.deleteByOrderId(orderId);
    }
}