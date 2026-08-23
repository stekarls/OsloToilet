package com.app.oslotoilet.paymentOption;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PaymentOptionResponseDto> createPaymentOption(@RequestBody @Valid PaymentOptionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentOptionService.createPaymentOption(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentOption(@PathVariable UUID id){
        paymentOptionService.deletePaymentOption(id);
        return ResponseEntity.noContent().build();
    }
}
