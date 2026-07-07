package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.model.MarketCrawlLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligencePushLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceReport;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceVerifyLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketKeywordTopic;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMarketStore {
    private final AtomicLong sourceIds = new AtomicLong(1);
    private final AtomicLong topicIds = new AtomicLong(1);
    private final AtomicLong itemIds = new AtomicLong(1);
    private final AtomicLong logIds = new AtomicLong(1);
    private final AtomicLong verifyLogIds = new AtomicLong(1);
    private final AtomicLong reportIds = new AtomicLong(1);
    private final AtomicLong pushIds = new AtomicLong(1);

    private final Map<Long, MarketInfoSource> sources = new LinkedHashMap<>();
    private final Map<Long, MarketKeywordTopic> topics = new LinkedHashMap<>();
    private final Map<Long, MarketIntelligence> items = new LinkedHashMap<>();
    private final Map<Long, MarketCrawlLog> crawlLogs = new LinkedHashMap<>();
    private final Map<Long, MarketIntelligenceVerifyLog> verifyLogs = new LinkedHashMap<>();
    private final Map<Long, MarketIntelligenceReport> reports = new LinkedHashMap<>();
    private final Map<Long, MarketIntelligencePushLog> pushLogs = new LinkedHashMap<>();

    public synchronized MarketInfoSource saveSource(MarketInfoSource source) {
        OffsetDateTime now = OffsetDateTime.now();
        if (source.getId() == null) {
            source.setId(sourceIds.getAndIncrement());
            source.setCreateTime(now);
        }
        source.setUpdateTime(now);
        sources.put(source.getId(), source);
        return source;
    }

    public synchronized MarketKeywordTopic saveTopic(MarketKeywordTopic topic) {
        OffsetDateTime now = OffsetDateTime.now();
        if (topic.getId() == null) {
            topic.setId(topicIds.getAndIncrement());
            topic.setCreateTime(now);
        }
        topic.setUpdateTime(now);
        topics.put(topic.getId(), topic);
        return topic;
    }

    public synchronized MarketIntelligence saveItem(MarketIntelligence item) {
        OffsetDateTime now = OffsetDateTime.now();
        if (item.getId() == null) {
            item.setId(itemIds.getAndIncrement());
            item.setCreateTime(now);
        }
        item.setUpdateTime(now);
        items.put(item.getId(), item);
        return item;
    }

    public synchronized MarketCrawlLog saveCrawlLog(MarketCrawlLog log) {
        if (log.getId() == null) {
            log.setId(logIds.getAndIncrement());
            log.setCreateTime(OffsetDateTime.now());
        }
        crawlLogs.put(log.getId(), log);
        return log;
    }

    public synchronized MarketIntelligenceVerifyLog saveVerifyLog(MarketIntelligenceVerifyLog log) {
        if (log.getId() == null) {
            log.setId(verifyLogIds.getAndIncrement());
            log.setCreateTime(OffsetDateTime.now());
        }
        verifyLogs.put(log.getId(), log);
        return log;
    }

    public synchronized MarketIntelligenceReport saveReport(MarketIntelligenceReport report) {
        if (report.getId() == null) {
            report.setId(reportIds.getAndIncrement());
            report.setCreateTime(OffsetDateTime.now());
        }
        reports.put(report.getId(), report);
        return report;
    }

    public synchronized MarketIntelligencePushLog savePushLog(MarketIntelligencePushLog log) {
        if (log.getId() == null) {
            log.setId(pushIds.getAndIncrement());
            log.setCreateTime(OffsetDateTime.now());
        }
        pushLogs.put(log.getId(), log);
        return log;
    }

    public List<MarketInfoSource> listSources() {
        return visible(sources);
    }

    public List<MarketKeywordTopic> listTopics() {
        return visible(topics);
    }

    public List<MarketIntelligence> listItems() {
        return visible(items).stream()
                .sorted(Comparator.comparing(MarketIntelligence::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public List<MarketCrawlLog> listCrawlLogs() {
        return new ArrayList<>(crawlLogs.values());
    }

    public List<MarketIntelligenceVerifyLog> listVerifyLogs() {
        return new ArrayList<>(verifyLogs.values());
    }

    public List<MarketIntelligencePushLog> listPushLogs() {
        return new ArrayList<>(pushLogs.values());
    }

    public List<MarketIntelligenceReport> listReports() {
        return new ArrayList<>(reports.values());
    }

    public Optional<MarketInfoSource> findSource(Long id) {
        return Optional.ofNullable(sources.get(id)).filter(source -> !source.isDeleted());
    }

    public Optional<MarketInfoSource> findSourceByCode(String sourceCode) {
        return sources.values().stream()
                .filter(source -> !source.isDeleted())
                .filter(source -> sourceCode != null && sourceCode.equals(source.getSourceCode()))
                .findFirst();
    }

    public Optional<MarketKeywordTopic> findTopic(Long id) {
        return Optional.ofNullable(topics.get(id)).filter(topic -> !topic.isDeleted());
    }

    public Optional<MarketIntelligence> findItem(Long id) {
        return Optional.ofNullable(items.get(id)).filter(item -> !item.isDeleted());
    }

    public Optional<MarketIntelligenceReport> findReport(Long id) {
        return Optional.ofNullable(reports.get(id));
    }

    public Optional<MarketCrawlLog> findCrawlLog(Long id) {
        return Optional.ofNullable(crawlLogs.get(id));
    }

    public boolean hasDuplicate(String urlHash, String contentHash) {
        return items.values().stream()
                .filter(item -> !item.isDeleted())
                .anyMatch(item -> safeEquals(item.getUrlHash(), urlHash) || safeEquals(item.getContentHash(), contentHash));
    }

    public List<MarketIntelligenceVerifyLog> listVerifyLogs(Long itemId) {
        return verifyLogs.values().stream().filter(log -> itemId.equals(log.getIntelligenceId())).toList();
    }

    public List<MarketIntelligencePushLog> listPushLogs(Long itemId) {
        return pushLogs.values().stream().filter(log -> itemId.equals(log.getIntelligenceId())).toList();
    }

    private static boolean safeEquals(String left, String right) {
        return left != null && left.equals(right);
    }

    private static <T> List<T> visible(Map<Long, T> map) {
        return new ArrayList<>(map.values());
    }
}
