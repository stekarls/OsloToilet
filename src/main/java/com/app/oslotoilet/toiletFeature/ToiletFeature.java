package com.app.oslotoilet.toiletFeature;

import com.app.oslotoilet.enums.SourceType;
import com.app.oslotoilet.feature.Feature;
import com.app.oslotoilet.toilet.Toilet;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "toilet_has_features", uniqueConstraints = @UniqueConstraint(columnNames = {"toilet_id", "feature_id"}))
public class ToiletFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Din nye, enkle ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    private OffsetDateTime verified;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private SourceType source;
}
