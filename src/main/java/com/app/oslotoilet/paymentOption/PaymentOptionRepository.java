package com.app.oslotoilet.paymentOption;

import com.app.oslotoilet.enums.PaymentCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentOptionRepository extends JpaRepository<PaymentOption, UUID>{
    boolean existsByCode(PaymentCode code);
    List<PaymentOption> findByCodeIn(Collection<PaymentCode> codes);
}
