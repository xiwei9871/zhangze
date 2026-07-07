package com.manqiaotechnology.executionosapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MarketIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketIntelligenceApplication.class, args);
    }
}
