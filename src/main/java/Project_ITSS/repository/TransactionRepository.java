package Project_ITSS.repository;

import Project_ITSS.entity.TransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("mainTransactionRepository")
public interface TransactionRepository extends JpaRepository<TransactionInfo, Long> {
    TransactionInfo findByOrderId(String orderId);
} 