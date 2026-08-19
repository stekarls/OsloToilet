package com.app.oslotoilet.toiletFeature;

import com.app.oslotoilet.feature.Feature;
import com.app.oslotoilet.toilet.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ToiletFeatureRepository extends JpaRepository<ToiletFeature, UUID> {
    boolean existsByToiletAndFeature(Toilet toilet, Feature feature);
    List<ToiletFeature> findByToilet(Toilet toilet);
}
