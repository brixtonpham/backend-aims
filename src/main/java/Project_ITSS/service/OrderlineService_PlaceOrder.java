package Project_ITSS.service;


import Project_ITSS.entity.Order;
import Project_ITSS.entity.Orderline;
import Project_ITSS.repository.OrderlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderlineService_PlaceOrder {
    @Autowired
    private OrderlineRepository orderlineRepository;

    public void saveOrderlines(Order order){
        for(Orderline orderline : order.getOrderLineList()){
            orderline.setOrder_id(order.getOrder_id());
            orderlineRepository.saveOrderline(orderline);
        }
    }


}
