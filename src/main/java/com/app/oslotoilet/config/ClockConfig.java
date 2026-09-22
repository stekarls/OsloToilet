package com.app.oslotoilet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    //Opening hours are Oslo local time. The server usually runs in UTC, so "now" must be read in Oslo's zone.
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Europe/Oslo"));
    }
}
