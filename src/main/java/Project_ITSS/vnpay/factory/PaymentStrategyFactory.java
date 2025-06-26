package Project_ITSS.vnpay.factory;

import Project_ITSS.vnpay.strategy.PaymentStrategy;

/**
 * Factory Pattern cho Payment Strategy Creation
 * Tạo payment strategy dựa trên provider type
 */
public interface PaymentStrategyFactory {
    PaymentStrategy createStrategy(String providerType);
} 