package com.app.oslotoilet.feature;


import com.app.oslotoilet.enums.FeatureCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "features")
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 32)
    private FeatureCode code;

    @Column(nullable = false, length = 255)
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

}
