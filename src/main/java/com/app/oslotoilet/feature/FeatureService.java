package com.app.oslotoilet.feature;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository){
        this.featureRepository = featureRepository;
    }

    public List<Feature> getAllFeatures(){
        return featureRepository.findAll();
    }
}
