package Project_ITSS.service;

import Project_ITSS.dto.OrderTrackingInfo;

public interface IProcessTrackingInfo {
    public OrderTrackingInfo processTracking(int order_id);
    public String getVersion();
} 