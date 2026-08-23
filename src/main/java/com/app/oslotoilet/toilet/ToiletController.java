package com.app.oslotoilet.toilet;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/toilets")
public class ToiletController {

    private final ToiletService toiletService;

    public ToiletController(ToiletService toiletService){
        this.toiletService = toiletService;
    }

    @GetMapping
    public ResponseEntity<List<ToiletResponseDto>>findAll(@RequestParam(required = false) String sort){
        return new ResponseEntity<>(toiletService.findAll(sort), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToiletResponseDto> findById(@PathVariable UUID id){
        return ResponseEntity.ok(toiletService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ToiletResponseDto> createToilet(@Valid @RequestBody ToiletRequestDto toiletRequestDto){
        return new ResponseEntity<>(toiletService.createToilet(toiletRequestDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToilet(@PathVariable UUID id){
        toiletService.deleteToilet(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ToiletResponseDto> updateToilet(@RequestBody @Valid ToiletUpdateDto toiletUpdateDto, @PathVariable UUID id){
        return ResponseEntity.ok(toiletService.updateToilet(toiletUpdateDto, id));
    }


}
