package com.manqiaotechnology.executionosapi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarketIntelligenceApplicationTest {
    @Test
    void appendsLocalHostsToProxyBypassLists() {
        System.setProperty("socksNonProxyHosts", "local|*.local");
        System.setProperty("http.nonProxyHosts", "local|*.local");
        System.setProperty("https.nonProxyHosts", "local|*.local");

        MarketIntelligenceApplication.configureLocalNonProxyHosts();

        assertThat(System.getProperty("socksNonProxyHosts")).contains("127.*", "localhost", "[::1]");
        assertThat(System.getProperty("http.nonProxyHosts")).contains("127.*", "localhost", "[::1]");
        assertThat(System.getProperty("https.nonProxyHosts")).contains("127.*", "localhost", "[::1]");
    }
}
