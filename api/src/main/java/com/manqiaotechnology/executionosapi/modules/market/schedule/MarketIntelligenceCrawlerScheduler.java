package com.manqiaotechnology.executionosapi.modules.market.schedule;

import com.manqiaotechnology.executionosapi.modules.market.enums.CrawlStatus;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketCrawlLog;
import com.manqiaotechnology.executionosapi.modules.market.service.InMemoryMarketStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@ConditionalOnProperty(prefix = "market.intelligence", name = "crawler-enabled", havingValue = "true", matchIfMissing = true)
public class MarketIntelligenceCrawlerScheduler {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final InMemoryMarketStore store;

    public MarketIntelligenceCrawlerScheduler(InMemoryMarketStore store) {
        this.store = store;
    }

    @Scheduled(fixedDelayString = "${market.intelligence.crawler-fixed-delay-ms:300000}")
    public void schedule() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        try {
            MarketCrawlLog log = new MarketCrawlLog();
            log.setBatchNo("SCHEDULE-" + UUID.randomUUID());
            log.setStartTime(OffsetDateTime.now());
            log.setEndTime(OffsetDateTime.now());
            log.setStatus(CrawlStatus.SUCCESS);
            log.setFetchedCount(0);
            log.setErrorMessage("MVP scheduler heartbeat; source-specific crawling is triggered via API or external import.");
            store.saveCrawlLog(log);
        } finally {
            running.set(false);
        }
    }
}
