package com.app.oslotoilet.toiletPaymentOption;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/toilets")
public class ToiletPaymentOptionController {

    private final ToiletPaymentOptionService toiletPaymentService;

    public ToiletPaymentOptionController(ToiletPaymentOptionService toiletPaymentService){
        this.toiletPaymentService = toiletPaymentService;
    }


    @GetMapping("/payment-options/all")
    public ResponseEntity<List<ToiletPaymentOptionResponseDto>> getAllToiletPaymentOptions(){
        return ResponseEntity.ok(toiletPaymentService.getAllToiletPaymentOptions());
    }

    @GetMapping("/{toiletId}/payment-options")
    public ResponseEntity<List<ToiletPaymentOptionResponseDto>> getPaymentOptionsForToilet(@PathVariable UUID toiletId) {
        List<ToiletPaymentOptionResponseDto> response = toiletPaymentService.getPaymentOptionsForToilet(toiletId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{toiletId}/payment-options")
    public ResponseEntity<ToiletPaymentOptionResponseDto> addPaymentOption(@PathVariable UUID toiletId, @RequestBody @Valid ToiletPaymentOptionRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(toiletPaymentService.addPaymentOption(toiletId, dto));
    }

    @PostMapping("/{toiletId}/payment-options/batch")
    public ResponseEntity<List<ToiletPaymentOptionResponseDto>> addPaymentOptions(@PathVariable UUID toiletId, @RequestBody @Valid ToiletPaymentOptionBulkRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(toiletPaymentService.addPaymentOptions(toiletId, dto));
    }

    @PatchMapping("/{toiletId}/payment-options/{toiletPaymentId}/verify")
    public ResponseEntity<ToiletPaymentOptionResponseDto> verifyPaymentOption(@PathVariable UUID toiletId, @PathVariable UUID toiletPaymentId){
        return ResponseEntity.ok(toiletPaymentService.verifyPaymentOption(toiletId, toiletPaymentId));
    }

    
    @DeleteMapping("/{toiletId}/payment-options/{toiletPaymentOptionId}")
    public ResponseEntity<Void> removePaymentOptionFromToilet(@PathVariable UUID toiletId, @PathVariable UUID toiletPaymentOptionId){
        toiletPaymentService.removePaymentOptionFromToilet(toiletId, toiletPaymentOptionId);
        return ResponseEntity.noContent().build();

        
    }
    
}
