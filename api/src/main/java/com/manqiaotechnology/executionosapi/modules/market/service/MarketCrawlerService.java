package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.enums.CrawlStatus;
import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.model.ExternalImportRequest;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketCrawlLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.manqiaotechnology.executionosapi.modules.market.service.crawler.MarketFetchedItem;
import com.manqiaotechnology.executionosapi.modules.market.service.crawler.MarketSourceFetcher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MarketCrawlerService {
    private final MarketStore store;
    private final MarketWorkflowService workflowService;
    private final List<MarketSourceFetcher> fetchers;

    public MarketCrawlerService(MarketStore store, MarketWorkflowService workflowService, List<MarketSourceFetcher> fetchers) {
        this.store = store;
        this.workflowService = workflowService;
        this.fetchers = fetchers;
    }

    public MarketCrawlLog runAll() {
        MarketCrawlLog aggregate = newLog(null, "BATCH-");
        for (MarketInfoSource source : runnableSources()) {
            MarketCrawlLog log = runSource(source);
            aggregate.setFetchedCount(aggregate.getFetchedCount() + log.getFetchedCount());
            aggregate.setCreatedCount(aggregate.getCreatedCount() + log.getCreatedCount());
            aggregate.setDuplicatedCount(aggregate.getDuplicatedCount() + log.getDuplicatedCount());
            aggregate.setMatchedCount(aggregate.getMatchedCount() + log.getMatchedCount());
            aggregate.setFailedCount(aggregate.getFailedCount() + log.getFailedCount());
        }
        aggregate.setEndTime(OffsetDateTime.now());
        aggregate.setStatus(aggregate.getFailedCount() > 0 ? CrawlStatus.PARTIAL : CrawlStatus.SUCCESS);
        return store.saveCrawlLog(aggregate);
    }

    public void runScheduledSources() {
        runnableSources().forEach(this::runSource);
    }

    public MarketCrawlLog runSource(Long sourceId) {
        return runSource(store.findSource(sourceId).orElseThrow());
    }

    public Map<String, Object> testFetch(Long sourceId) {
        MarketInfoSource source = store.findSource(sourceId).orElseThrow();
        try {
            List<MarketFetchedItem> items = fetch(source);
            source.setLastFetchTime(OffsetDateTime.now());
            source.setLastFetchStatus(CrawlStatus.SUCCESS.name());
            source.setLastErrorMessage(null);
            store.saveSource(source);
            return Map.of(
                    "sourceId", sourceId,
                    "status", CrawlStatus.SUCCESS.name(),
                    "fetchedCount", items.size(),
                    "sampleItems", items.stream().limit(5).map(this::sampleItem).toList()
            );
        } catch (Exception ex) {
            source.setLastFetchTime(OffsetDateTime.now());
            source.setLastFetchStatus(CrawlStatus.FAILED.name());
            source.setLastErrorMessage(ex.getMessage());
            store.saveSource(source);
            return Map.of(
                    "sourceId", sourceId,
                    "status", CrawlStatus.FAILED.name(),
                    "fetchedCount", 0,
                    "sampleItems", List.of(),
                    "errorMessage", ex.getMessage()
            );
        }
    }

    private MarketCrawlLog runSource(MarketInfoSource source) {
        MarketCrawlLog log = newLog(source.getId(), "CRAWL-");
        try {
            List<MarketFetchedItem> fetchedItems = fetch(source);
            MarketWorkflowService.ImportResult result = workflowService.importExternalItems(new ExternalImportRequest(
                    source.getSourceCode(),
                    fetchedItems.stream().map(this::toExternalItem).toList()
            ));
            log.setFetchedCount(result.fetchedCount());
            log.setCreatedCount(result.createdCount());
            log.setDuplicatedCount(result.duplicatedCount());
            log.setMatchedCount(result.matchedCount());
            log.setFailedCount(result.failedCount());
            log.setStatus(result.failedCount() > 0 ? CrawlStatus.PARTIAL : CrawlStatus.SUCCESS);
            log.setEndTime(OffsetDateTime.now());
            source.setLastFetchTime(log.getEndTime());
            source.setLastFetchStatus(log.getStatus().name());
            source.setLastErrorMessage(null);
            source.setLastCrawlCursor(log.getEndTime().toString());
        } catch (Exception ex) {
            log.setStatus(CrawlStatus.FAILED);
            log.setFailedCount(1);
            log.setEndTime(OffsetDateTime.now());
            log.setErrorMessage(ex.getMessage());
            source.setLastFetchTime(log.getEndTime());
            source.setLastFetchStatus(CrawlStatus.FAILED.name());
            source.setLastErrorMessage(ex.getMessage());
        }
        store.saveSource(source);
        return store.saveCrawlLog(log);
    }

    private List<MarketInfoSource> runnableSources() {
        return store.listSources().stream()
                .filter(MarketInfoSource::isEnabled)
                .filter(source -> source.getFetchMode() == FetchMode.RSS
                        || source.getFetchMode() == FetchMode.HTML
                        || source.getFetchMode() == FetchMode.SEARCH_PAGE)
                .toList();
    }

    private List<MarketFetchedItem> fetch(MarketInfoSource source) throws Exception {
        MarketSourceFetcher fetcher = fetchers.stream()
                .filter(candidate -> candidate.supports(source.getFetchMode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported fetch mode: " + source.getFetchMode()));
        return fetcher.fetch(source);
    }

    private ExternalImportRequest.Item toExternalItem(MarketFetchedItem item) {
        return new ExternalImportRequest.Item(
                item.title(),
                item.url(),
                item.publishTime(),
                item.author(),
                item.content(),
                item.html(),
                item.tags()
        );
    }

    private Map<String, Object> sampleItem(MarketFetchedItem item) {
        Map<String, Object> sample = new LinkedHashMap<>();
        sample.put("title", item.title());
        sample.put("url", item.url());
        sample.put("publishTime", item.publishTime());
        sample.put("contentExcerpt", item.content() == null || item.content().length() <= 120 ? item.content() : item.content().substring(0, 120));
        return sample;
    }

    private static MarketCrawlLog newLog(Long sourceId, String prefix) {
        MarketCrawlLog log = new MarketCrawlLog();
        log.setSourceId(sourceId);
        log.setBatchNo(prefix + UUID.randomUUID());
        log.setStartTime(OffsetDateTime.now());
        log.setStatus(CrawlStatus.RUNNING);
        return log;
    }
}
