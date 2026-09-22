package com.app.oslotoilet.toilet;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<List<ToiletResponseDto>> findAll(@RequestParam(required = false) String sort){
        return ResponseEntity.ok(toiletService.findAll(sort));
    }

    @GetMapping("/markers")
    public ResponseEntity<List<ToiletMarkerResponseDto>> findAllMarkers(@RequestParam(required = false) Boolean hasFee,
                                                                        @RequestParam(defaultValue = "false") boolean openNow){
        return ResponseEntity.ok(toiletService.findAllMarkers(hasFee, openNow));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToiletResponseDto> findById(@PathVariable UUID id){
        return ResponseEntity.ok(toiletService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ToiletResponseDto> createToilet(@Valid @RequestBody ToiletRequestDto toiletRequestDto){
        ToiletResponseDto created = toiletService.createToilet(toiletRequestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToilet(@PathVariable UUID id){
        toiletService.deleteToilet(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ToiletResponseDto> updateToilet(@RequestBody @Valid ToiletUpdateDto toiletUpdateDto, @PathVariable UUID id){
        return ResponseEntity.ok(toiletService.updateToilet(toiletUpdateDto, id));
    }


}
