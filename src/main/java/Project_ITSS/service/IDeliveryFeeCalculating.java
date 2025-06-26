package Project_ITSS.service;

import Project_ITSS.dto.CalculateFeeDTO;

public interface IDeliveryFeeCalculating {
    public int[] CalculateDeliveryFee(CalculateFeeDTO calculateFeeDTO);
    public String getVersion();
} 