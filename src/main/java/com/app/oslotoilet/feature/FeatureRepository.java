package com.app.oslotoilet.feature;

import com.app.oslotoilet.enums.FeatureCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, UUID> {
    boolean existsByFeatureCode(FeatureCode featureCode);
}
