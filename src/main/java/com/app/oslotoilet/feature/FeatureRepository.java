package com.app.oslotoilet.feature;

import com.app.oslotoilet.enums.FeatureCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, UUID> {
    boolean existsByFeatureCode(FeatureCode featureCode);
    List<Feature> findByFeatureCodeIn(Collection<FeatureCode> featureCodes);
}
