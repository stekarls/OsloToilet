package com.app.oslotoilet.toilet;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/toilet")
public class ToiletController {

    private final ToiletService toiletService;

    public ToiletController(ToiletService toiletService){
        this.toiletService = toiletService;
    }

    @GetMapping
    public ResponseEntity<List<ToiletResponseDto>>findAll(@RequestParam String sort){
        return new ResponseEntity<>(toiletService.findAll(sort), HttpStatus.OK);
    }

    //TODO: do i need different response if not found?
    @GetMapping("{id}")
    public ResponseEntity<ToiletResponseDto> findById(@PathVariable UUID id){
        return ResponseEntity.ok(toiletService.findById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ToiletResponseDto> createToilet(@Valid @RequestBody ToiletRequestDto toiletRequestDto){
        return new ResponseEntity<>(toiletService.createToilet(toiletRequestDto), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteToilet(UUID id){
        toiletService.deleteToilet(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToiletResponseDto> updateToilet(ToiletUpdateDto toiletUpdateDto, @PathVariable UUID id){
        return ResponseEntity.ok(toiletService.updateToilet(toiletUpdateDto, id));
    }


}
