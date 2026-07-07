package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.IntelligenceStatus;
import com.manqiaotechnology.executionosapi.modules.market.model.ExternalImportRequest;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceReport;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceVerifyLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketKeywordTopic;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MarketWorkflowService {
    private final InMemoryMarketStore store;
    private final MarketKeywordMatchService keywordMatchService;
    private final MarketDeduplicateService deduplicateService;
    private final MarketAiSummaryService aiSummaryService;
    private final MarketNotificationService notificationService;

    public MarketWorkflowService(
            InMemoryMarketStore store,
            MarketKeywordMatchService keywordMatchService,
            MarketDeduplicateService deduplicateService,
            MarketAiSummaryService aiSummaryService,
            MarketNotificationService notificationService
    ) {
        this.store = store;
        this.keywordMatchService = keywordMatchService;
        this.deduplicateService = deduplicateService;
        this.aiSummaryService = aiSummaryService;
        this.notificationService = notificationService;
    }

    public ImportResult importExternalItems(ExternalImportRequest request) {
        MarketInfoSource source = store.findSourceByCode(request.sourceCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown sourceCode: " + request.sourceCode()));
        int fetched = 0;
        int created = 0;
        int duplicated = 0;
        int matched = 0;
        int failed = 0;
        for (ExternalImportRequest.Item imported : request.items()) {
            fetched++;
            try {
                String content = cleanContent(imported.content(), imported.html());
                String urlHash = deduplicateService.hashUrl(imported.url());
                String contentHash = deduplicateService.hashContent(content);
                if (store.hasDuplicate(urlHash, contentHash)) {
                    duplicated++;
                    continue;
                }
                MarketKeywordMatchService.MatchResult match = keywordMatchService.match(
                        imported.title(),
                        content,
                        store.listTopics()
                );
                if (!match.matched()) {
                    continue;
                }
                matched++;
                MarketIntelligence item = buildItem(source, imported, content, match);
                store.saveItem(item);
                aiSummaryService.summarize(item);
                store.saveItem(item);
                if (item.getStatus() == IntelligenceStatus.PENDING_VERIFY
                        || item.getManualImportanceLevel().rank() >= ImportanceLevel.HIGH.rank()) {
                    notificationService.pushToOwner(item, "新的市场情报待核验");
                }
                created++;
            } catch (RuntimeException ex) {
                failed++;
            }
        }
        return new ImportResult(fetched, created, duplicated, matched, failed);
    }

    public MarketIntelligence resummarize(Long itemId) {
        MarketIntelligence item = store.findItem(itemId).orElseThrow();
        aiSummaryService.summarize(item);
        store.saveItem(item);
        return item;
    }

    public MarketIntelligence verify(Long itemId, IntelligenceStatus afterStatus, ImportanceLevel importanceLevel,
                                    String remark, String actionSuggestion, Long operatorId) {
        MarketIntelligence item = store.findItem(itemId).orElseThrow();
        IntelligenceStatus before = item.getStatus();
        item.setStatus(afterStatus);
        item.setManualImportanceLevel(importanceLevel);
        item.setVerifyRemark(remark);
        item.setVerifyUserId(operatorId);
        item.setVerifyTime(OffsetDateTime.now());
        store.saveItem(item);

        MarketIntelligenceVerifyLog log = new MarketIntelligenceVerifyLog();
        log.setIntelligenceId(itemId);
        log.setBeforeStatus(before);
        log.setAfterStatus(afterStatus);
        log.setImportanceLevel(importanceLevel);
        log.setVerifyRemark(remark);
        log.setActionSuggestion(actionSuggestion);
        log.setOperatorId(operatorId);
        store.saveVerifyLog(log);
        return item;
    }

    public MarketIntelligenceReport report(Long itemId, String title, String content, Long reportToUserId, Long reportByUserId) {
        MarketIntelligence item = store.findItem(itemId).orElseThrow();
        MarketIntelligenceReport report = new MarketIntelligenceReport();
        report.setIntelligenceId(itemId);
        report.setReportTitle(title);
        report.setReportContent(content);
        report.setReportToUserId(reportToUserId);
        report.setReportByUserId(reportByUserId);
        report.setReportTime(OffsetDateTime.now());
        store.saveReport(report);

        item.setReported(true);
        item.setReportTime(report.getReportTime());
        item.setStatus(IntelligenceStatus.REPORTED);
        store.saveItem(item);
        notificationService.pushToManager(item, reportToUserId, "市场情报上报");
        return report;
    }

    private MarketIntelligence buildItem(
            MarketInfoSource source,
            ExternalImportRequest.Item imported,
            String content,
            MarketKeywordMatchService.MatchResult match
    ) {
        MarketIntelligence item = new MarketIntelligence();
        item.setSourceId(source.getId());
        item.setSourceName(source.getSourceName());
        item.setSourceType(source.getSourceType());
        item.setTitle(imported.title());
        item.setUrl(imported.url());
        item.setUrlHash(deduplicateService.hashUrl(imported.url()));
        item.setTitleHash(deduplicateService.hashTitle(imported.title()));
        item.setContentHash(deduplicateService.hashContent(content));
        item.setPublishTime(imported.publishTime());
        item.setCrawlTime(OffsetDateTime.now());
        item.setRawContent(content);
        item.setContentExcerpt(content.length() > 180 ? content.substring(0, 180) : content);
        item.setMatchedKeywords(match.matchedKeywords());
        item.setMatchedTopicIds(match.matchedTopicIds());
        item.setManualImportanceLevel(match.highestImportance());
        applyOwnerAndManager(item, source, match.matchedTopicIds());
        return item;
    }

    private void applyOwnerAndManager(MarketIntelligence item, MarketInfoSource source, List<Long> matchedTopicIds) {
        List<MarketKeywordTopic> matchedTopics = store.listTopics().stream()
                .filter(topic -> matchedTopicIds.contains(topic.getId()))
                .sorted(Comparator.comparing(topic -> topic.getImportanceLevel().rank(), Comparator.reverseOrder()))
                .toList();
        item.setOwnerUserId(matchedTopics.stream().map(MarketKeywordTopic::getOwnerUserId).filter(id -> id != null).findFirst()
                .orElse(source.getDefaultOwnerUserId()));
        item.setManagerUserId(matchedTopics.stream().map(MarketKeywordTopic::getManagerUserId).filter(id -> id != null).findFirst()
                .orElse(source.getDefaultManagerUserId()));
    }

    private static String cleanContent(String content, String html) {
        if (content != null && !content.isBlank()) {
            return content.trim();
        }
        if (html == null || html.isBlank()) {
            return "";
        }
        return Jsoup.parse(html).text();
    }

    public record ImportResult(int fetchedCount, int createdCount, int duplicatedCount, int matchedCount, int failedCount) {
    }
}
