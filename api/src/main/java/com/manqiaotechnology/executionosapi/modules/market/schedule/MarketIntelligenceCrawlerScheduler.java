package com.manqiaotechnology.executionosapi.modules.market.schedule;

import com.manqiaotechnology.executionosapi.modules.market.service.MarketCrawlerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@ConditionalOnProperty(prefix = "market.intelligence", name = "crawler-enabled", havingValue = "true", matchIfMissing = true)
public class MarketIntelligenceCrawlerScheduler {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final MarketCrawlerService crawlerService;

    public MarketIntelligenceCrawlerScheduler(MarketCrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @Scheduled(fixedDelayString = "${market.intelligence.crawler-fixed-delay-ms:300000}")
    public void schedule() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        try {
            crawlerService.runScheduledSources();
        } finally {
            running.set(false);
        }
    }
}
