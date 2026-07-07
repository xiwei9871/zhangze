package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.model.MarketCrawlLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligencePushLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceReport;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceVerifyLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketKeywordTopic;

import java.util.List;
import java.util.Optional;

public interface MarketStore {
    MarketInfoSource saveSource(MarketInfoSource source);

    MarketKeywordTopic saveTopic(MarketKeywordTopic topic);

    MarketIntelligence saveItem(MarketIntelligence item);

    MarketCrawlLog saveCrawlLog(MarketCrawlLog log);

    MarketIntelligenceVerifyLog saveVerifyLog(MarketIntelligenceVerifyLog log);

    MarketIntelligenceReport saveReport(MarketIntelligenceReport report);

    MarketIntelligencePushLog savePushLog(MarketIntelligencePushLog log);

    List<MarketInfoSource> listSources();

    List<MarketKeywordTopic> listTopics();

    List<MarketIntelligence> listItems();

    List<MarketCrawlLog> listCrawlLogs();

    List<MarketIntelligenceVerifyLog> listVerifyLogs();

    List<MarketIntelligencePushLog> listPushLogs();

    List<MarketIntelligenceReport> listReports();

    Optional<MarketInfoSource> findSource(Long id);

    Optional<MarketInfoSource> findSourceByCode(String sourceCode);

    Optional<MarketKeywordTopic> findTopic(Long id);

    Optional<MarketIntelligence> findItem(Long id);

    Optional<MarketIntelligenceReport> findReport(Long id);

    Optional<MarketCrawlLog> findCrawlLog(Long id);

    boolean hasDuplicate(String urlHash, String contentHash);

    List<MarketIntelligenceVerifyLog> listVerifyLogs(Long itemId);

    List<MarketIntelligencePushLog> listPushLogs(Long itemId);
}
