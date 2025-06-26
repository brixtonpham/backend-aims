package Project_ITSS.vnpay.repository;

import Project_ITSS.vnpay.entity.TransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("vnpayTransactionRepository")
public interface TransactionRepository extends JpaRepository<TransactionInfo, Long> {
    TransactionInfo findByOrderId(String orderId);
} 