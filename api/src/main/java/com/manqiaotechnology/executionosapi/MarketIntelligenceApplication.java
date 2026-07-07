package com.manqiaotechnology.executionosapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.manqiaotechnology.executionosapi.modules.market.mapper")
@SpringBootApplication
public class MarketIntelligenceApplication {
    private static final String LOCAL_NON_PROXY_HOSTS = "localhost|127.*|[::1]";

    public static void main(String[] args) {
        configureLocalNonProxyHosts();
        SpringApplication.run(MarketIntelligenceApplication.class, args);
    }

    static void configureLocalNonProxyHosts() {
        appendNonProxyHosts("socksNonProxyHosts");
        appendNonProxyHosts("http.nonProxyHosts");
        appendNonProxyHosts("https.nonProxyHosts");
    }

    private static void appendNonProxyHosts(String propertyName) {
        String current = System.getProperty(propertyName);
        if (current == null || current.isBlank()) {
            System.setProperty(propertyName, LOCAL_NON_PROXY_HOSTS);
            return;
        }
        if (!current.contains("127.*")) {
            System.setProperty(propertyName, current + "|" + LOCAL_NON_PROXY_HOSTS);
        }
    }
}
