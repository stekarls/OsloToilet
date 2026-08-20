package com.app.oslotoilet.toiletPaymentOption;

import com.app.oslotoilet.paymentOption.PaymentOption;
import com.app.oslotoilet.toilet.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ToiletPaymentOptionRepository extends JpaRepository<ToiletPaymentOption, UUID> {
    boolean existsByToiletAndPaymentOption(Toilet toilet, PaymentOption paymentOption);
    List<ToiletPaymentOption> findByToilet(Toilet toilet);
}
