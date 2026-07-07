package com.manqiaotechnology.executionosapi.modules.market.controller;

import com.manqiaotechnology.executionosapi.common.PageResult;
import com.manqiaotechnology.executionosapi.common.Result;
import com.manqiaotechnology.executionosapi.modules.market.enums.CrawlStatus;
import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.IntelligenceStatus;
import com.manqiaotechnology.executionosapi.modules.market.model.ExternalImportRequest;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketCrawlLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceReport;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketKeywordTopic;
import com.manqiaotechnology.executionosapi.modules.market.service.InMemoryMarketStore;
import com.manqiaotechnology.executionosapi.modules.market.service.MarketKeywordMatchService;
import com.manqiaotechnology.executionosapi.modules.market.service.MarketWorkflowService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/market/intelligence")
public class MarketIntelligenceController {
    private final InMemoryMarketStore store;
    private final MarketWorkflowService workflowService;
    private final MarketKeywordMatchService keywordMatchService;

    public MarketIntelligenceController(
            InMemoryMarketStore store,
            MarketWorkflowService workflowService,
            MarketKeywordMatchService keywordMatchService
    ) {
        this.store = store;
        this.workflowService = workflowService;
        this.keywordMatchService = keywordMatchService;
    }

    @GetMapping("/sources")
    public Result<PageResult<MarketInfoSource>> listSources() {
        return Result.ok(PageResult.of(store.listSources(), 1, 100));
    }

    @PostMapping("/sources")
    public Result<MarketInfoSource> createSource(@RequestBody MarketInfoSource source) {
        return Result.ok(store.saveSource(source));
    }

    @GetMapping("/sources/{id}")
    public Result<MarketInfoSource> getSource(@PathVariable Long id) {
        return Result.ok(store.findSource(id).orElseThrow());
    }

    @PutMapping("/sources/{id}")
    public Result<MarketInfoSource> updateSource(@PathVariable Long id, @RequestBody MarketInfoSource source) {
        source.setId(id);
        return Result.ok(store.saveSource(source));
    }

    @DeleteMapping("/sources/{id}")
    public Result<Boolean> deleteSource(@PathVariable Long id) {
        MarketInfoSource source = store.findSource(id).orElseThrow();
        source.setDeleted(true);
        store.saveSource(source);
        return Result.ok(true);
    }

    @PostMapping("/sources/{id}/enable")
    public Result<MarketInfoSource> enableSource(@PathVariable Long id) {
        MarketInfoSource source = store.findSource(id).orElseThrow();
        source.setEnabled(true);
        return Result.ok(store.saveSource(source));
    }

    @PostMapping("/sources/{id}/disable")
    public Result<MarketInfoSource> disableSource(@PathVariable Long id) {
        MarketInfoSource source = store.findSource(id).orElseThrow();
        source.setEnabled(false);
        return Result.ok(store.saveSource(source));
    }

    @PostMapping("/sources/{id}/test-fetch")
    public Result<Map<String, Object>> testFetch(@PathVariable Long id) {
        MarketInfoSource source = store.findSource(id).orElseThrow();
        source.setLastFetchTime(OffsetDateTime.now());
        source.setLastFetchStatus("SUCCESS");
        store.saveSource(source);
        return Result.ok(Map.of("sourceId", id, "status", "SUCCESS", "message", "MVP test fetch completed"));
    }

    @GetMapping("/topics")
    public Result<PageResult<MarketKeywordTopic>> listTopics() {
        return Result.ok(PageResult.of(store.listTopics(), 1, 100));
    }

    @PostMapping("/topics")
    public Result<MarketKeywordTopic> createTopic(@RequestBody MarketKeywordTopic topic) {
        return Result.ok(store.saveTopic(topic));
    }

    @GetMapping("/topics/{id}")
    public Result<MarketKeywordTopic> getTopic(@PathVariable Long id) {
        return Result.ok(store.findTopic(id).orElseThrow());
    }

    @PutMapping("/topics/{id}")
    public Result<MarketKeywordTopic> updateTopic(@PathVariable Long id, @RequestBody MarketKeywordTopic topic) {
        topic.setId(id);
        return Result.ok(store.saveTopic(topic));
    }

    @DeleteMapping("/topics/{id}")
    public Result<Boolean> deleteTopic(@PathVariable Long id) {
        MarketKeywordTopic topic = store.findTopic(id).orElseThrow();
        topic.setDeleted(true);
        store.saveTopic(topic);
        return Result.ok(true);
    }

    @PostMapping("/topics/{id}/enable")
    public Result<MarketKeywordTopic> enableTopic(@PathVariable Long id) {
        MarketKeywordTopic topic = store.findTopic(id).orElseThrow();
        topic.setEnabled(true);
        return Result.ok(store.saveTopic(topic));
    }

    @PostMapping("/topics/{id}/disable")
    public Result<MarketKeywordTopic> disableTopic(@PathVariable Long id) {
        MarketKeywordTopic topic = store.findTopic(id).orElseThrow();
        topic.setEnabled(false);
        return Result.ok(store.saveTopic(topic));
    }

    @PostMapping("/topics/import")
    public Result<List<MarketKeywordTopic>> importTopics(@RequestBody List<MarketKeywordTopic> topics) {
        return Result.ok(topics.stream().map(store::saveTopic).toList());
    }

    @PostMapping("/topics/preview-match")
    public Result<MarketKeywordMatchService.MatchResult> previewMatch(@RequestBody PreviewMatchRequest request) {
        return Result.ok(keywordMatchService.match(request.title(), request.content(), store.listTopics()));
    }

    @PostMapping("/crawl/run")
    public Result<MarketCrawlLog> runCrawl() {
        return Result.ok(createCrawlLog(null, CrawlStatus.SUCCESS, "Manual all-source crawl accepted by MVP scheduler"));
    }

    @PostMapping("/crawl/run/{sourceId}")
    public Result<MarketCrawlLog> runSourceCrawl(@PathVariable Long sourceId) {
        return Result.ok(createCrawlLog(sourceId, CrawlStatus.SUCCESS, "Manual source crawl accepted by MVP scheduler"));
    }

    @GetMapping("/crawl/logs")
    public Result<PageResult<MarketCrawlLog>> listCrawlLogs() {
        return Result.ok(PageResult.of(store.listCrawlLogs(), 1, 100));
    }

    @GetMapping("/crawl/logs/{id}")
    public Result<MarketCrawlLog> getCrawlLog(@PathVariable Long id) {
        return Result.ok(store.findCrawlLog(id).orElseThrow());
    }

    @GetMapping("/items")
    public Result<PageResult<MarketIntelligence>> listItems() {
        return Result.ok(PageResult.of(store.listItems(), 1, 100));
    }

    @GetMapping("/items/{id}")
    public Result<MarketIntelligence> getItem(@PathVariable Long id) {
        return Result.ok(store.findItem(id).orElseThrow());
    }

    @PostMapping("/items/{id}/resummarize")
    public Result<MarketIntelligence> resummarize(@PathVariable Long id) {
        return Result.ok(workflowService.resummarize(id));
    }

    @PostMapping("/items/{id}/assign")
    public Result<MarketIntelligence> assign(@PathVariable Long id, @RequestBody AssignRequest request) {
        MarketIntelligence item = store.findItem(id).orElseThrow();
        item.setOwnerUserId(request.ownerUserId());
        item.setManagerUserId(request.managerUserId());
        return Result.ok(store.saveItem(item));
    }

    @PostMapping("/items/{id}/verify")
    public Result<MarketIntelligence> verify(@PathVariable Long id, @RequestBody VerifyRequest request) {
        return Result.ok(workflowService.verify(
                id,
                request.afterStatus(),
                request.importanceLevel(),
                request.verifyRemark(),
                request.actionSuggestion(),
                request.operatorId()
        ));
    }

    @GetMapping("/items/{id}/verify-logs")
    public Result<?> verifyLogs(@PathVariable Long id) {
        return Result.ok(store.listVerifyLogs(id));
    }

    @GetMapping("/items/{id}/push-logs")
    public Result<?> pushLogs(@PathVariable Long id) {
        return Result.ok(store.listPushLogs(id));
    }

    @PostMapping("/items/{id}/report")
    public Result<MarketIntelligenceReport> report(@PathVariable Long id, @RequestBody ReportRequest request) {
        return Result.ok(workflowService.report(
                id,
                request.reportTitle(),
                request.reportContent(),
                request.reportToUserId(),
                request.reportByUserId()
        ));
    }

    @GetMapping("/reports")
    public Result<PageResult<MarketIntelligenceReport>> reports() {
        return Result.ok(PageResult.of(store.listReports(), 1, 100));
    }

    @GetMapping("/reports/{id}")
    public Result<MarketIntelligenceReport> reportDetail(@PathVariable Long id) {
        return Result.ok(store.findReport(id).orElseThrow());
    }

    @GetMapping("/dashboard/summary")
    public Result<Map<String, Object>> dashboardSummary() {
        List<MarketIntelligence> items = store.listItems();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("todayNewCount", items.stream().filter(item -> isToday(item.getCreateTime())).count());
        summary.put("pendingVerifyCount", countStatus(items, IntelligenceStatus.PENDING_VERIFY));
        summary.put("validCount", countStatus(items, IntelligenceStatus.VALID));
        summary.put("followUpCount", countStatus(items, IntelligenceStatus.FOLLOW_UP));
        summary.put("reportedCount", items.stream().filter(MarketIntelligence::isReported).count());
        summary.put("highImportanceCount", items.stream().filter(item -> item.getManualImportanceLevel().rank() >= ImportanceLevel.HIGH.rank()).count());
        return Result.ok(summary);
    }

    @GetMapping("/dashboard/trend")
    public Result<Map<String, Long>> dashboardTrend() {
        return Result.ok(store.listItems().stream().collect(Collectors.groupingBy(
                item -> item.getCreateTime() == null ? "unknown" : item.getCreateTime().toLocalDate().toString(),
                LinkedHashMap::new,
                Collectors.counting()
        )));
    }

    @GetMapping("/dashboard/source-distribution")
    public Result<Map<String, Long>> sourceDistribution() {
        return Result.ok(store.listItems().stream().collect(Collectors.groupingBy(
                item -> item.getSourceType().name(),
                LinkedHashMap::new,
                Collectors.counting()
        )));
    }

    @GetMapping("/dashboard/topic-rank")
    public Result<Map<Long, Long>> topicRank() {
        return Result.ok(store.listItems().stream()
                .flatMap(item -> item.getMatchedTopicIds().stream())
                .collect(Collectors.groupingBy(id -> id, LinkedHashMap::new, Collectors.counting())));
    }

    @GetMapping("/dashboard/keyword-rank")
    public Result<Map<String, Long>> keywordRank() {
        return Result.ok(store.listItems().stream()
                .flatMap(item -> item.getMatchedKeywords().stream())
                .collect(Collectors.groupingBy(keyword -> keyword, LinkedHashMap::new, Collectors.counting())));
    }

    @GetMapping("/dashboard/high-value-items")
    public Result<List<MarketIntelligence>> highValueItems() {
        return Result.ok(store.listItems().stream()
                .filter(item -> item.getManualImportanceLevel().rank() >= ImportanceLevel.HIGH.rank())
                .limit(10)
                .toList());
    }

    @GetMapping("/dashboard/pending-items")
    public Result<List<MarketIntelligence>> pendingItems() {
        return Result.ok(store.listItems().stream()
                .filter(item -> item.getStatus() == IntelligenceStatus.PENDING_VERIFY)
                .limit(10)
                .toList());
    }

    @PostMapping("/external/items/import")
    public Result<MarketWorkflowService.ImportResult> externalImport(@RequestBody ExternalImportRequest request) {
        return Result.ok(workflowService.importExternalItems(request));
    }

    @GetMapping("/external/import-logs")
    public Result<PageResult<MarketCrawlLog>> externalImportLogs() {
        return Result.ok(PageResult.of(store.listCrawlLogs(), 1, 100));
    }

    private MarketCrawlLog createCrawlLog(Long sourceId, CrawlStatus status, String message) {
        MarketCrawlLog log = new MarketCrawlLog();
        log.setSourceId(sourceId);
        log.setBatchNo("BATCH-" + UUID.randomUUID());
        log.setStartTime(OffsetDateTime.now());
        log.setEndTime(OffsetDateTime.now());
        log.setStatus(status);
        log.setErrorMessage(message);
        return store.saveCrawlLog(log);
    }

    private static boolean isToday(OffsetDateTime time) {
        return time != null && time.toLocalDate().equals(LocalDate.now());
    }

    private static long countStatus(List<MarketIntelligence> items, IntelligenceStatus status) {
        return items.stream().filter(item -> item.getStatus() == status).count();
    }

    public record PreviewMatchRequest(String title, String content) {
    }

    public record AssignRequest(Long ownerUserId, Long managerUserId) {
    }

    public record VerifyRequest(
            IntelligenceStatus afterStatus,
            ImportanceLevel importanceLevel,
            String verifyRemark,
            String actionSuggestion,
            Long operatorId
    ) {
    }

    public record ReportRequest(String reportTitle, String reportContent, Long reportToUserId, Long reportByUserId) {
    }
}
