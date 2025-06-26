package Project_ITSS.vnpay.strategy;

import Project_ITSS.vnpay.dto.PaymentRequest;
import Project_ITSS.vnpay.dto.QueryRequest;
import Project_ITSS.vnpay.dto.RefundRequest;
import Project_ITSS.vnpay.service.VNPayService.PaymentResponse;
import Project_ITSS.vnpay.service.VNPayService.QueryResponse;
import Project_ITSS.vnpay.service.VNPayService.RefundResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Strategy Pattern cho Payment Providers
 * Interface cho các payment strategy khác nhau
 */
public interface PaymentStrategy {
    PaymentResponse createPayment(PaymentRequest request, HttpServletRequest servletRequest);
    QueryResponse queryTransaction(QueryRequest request, HttpServletRequest servletRequest);
    RefundResponse refundTransaction(RefundRequest request, HttpServletRequest servletRequest);
    String getProviderName();
} 