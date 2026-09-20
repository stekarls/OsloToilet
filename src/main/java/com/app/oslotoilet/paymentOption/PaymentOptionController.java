package com.app.oslotoilet.paymentOption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment-options")
public class PaymentOptionController{
    private final PaymentOptionService paymentOptionService;

    public PaymentOptionController(PaymentOptionService paymentOptionService){
        this.paymentOptionService = paymentOptionService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentOptionResponseDto>> getAllPaymentOptions(){
        return ResponseEntity.ok(paymentOptionService.getAllPaymentOptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentOptionResponseDto> getPaymentOptionById(@PathVariable UUID id){
        return ResponseEntity.ok(paymentOptionService.getPaymentOptionById(id));
    }
}
