package Project_ITSS.service;

import Project_ITSS.vnpay.dto.PaymentRequest;
import Project_ITSS.vnpay.dto.QueryRequest;
import Project_ITSS.vnpay.dto.RefundRequest;
import Project_ITSS.vnpay.service.VNPayService.PaymentResponse;
import Project_ITSS.vnpay.service.VNPayService.QueryResponse;
import Project_ITSS.vnpay.service.VNPayService.RefundResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface cho payment service - giải quyết tight coupling
 * Cho phép thay đổi payment provider mà không sửa code hiện tại
 */
public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request, HttpServletRequest servletRequest);
    QueryResponse queryTransaction(QueryRequest request, HttpServletRequest servletRequest);
    RefundResponse refundTransaction(RefundRequest request, HttpServletRequest servletRequest);
} 