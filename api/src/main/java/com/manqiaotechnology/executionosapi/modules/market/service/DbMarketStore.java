package com.manqiaotechnology.executionosapi.modules.market.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manqiaotechnology.executionosapi.modules.market.mapper.MarketCrawlLogMapper;
import com.manqiaotechnology.executionosapi.modules.market.mapper.MarketInfoSourceMapper;
import com.manqiaotechnology.executionosapi.modules.market.mapper.MarketIntelligenceMapper;
import com.manqiaotechnology.executionosapi.modules.market.mapper.MarketIntelligencePushLogMapper;
import com.manqiaotechnology.executionosapi.modules.market.mapper.MarketIntelligenceReportMapper;
import com.manqiaotechnology.executionosapi.modules.market.mapper.MarketIntelligenceVerifyLogMapper;
import com.manqiaotechnology.executionosapi.modules.market.mapper.MarketKeywordTopicMapper;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketCrawlLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligencePushLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceReport;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligenceVerifyLog;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketKeywordTopic;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class DbMarketStore implements MarketStore {
    private final MarketInfoSourceMapper sourceMapper;
    private final MarketKeywordTopicMapper topicMapper;
    private final MarketIntelligenceMapper itemMapper;
    private final MarketCrawlLogMapper crawlLogMapper;
    private final MarketIntelligenceVerifyLogMapper verifyLogMapper;
    private final MarketIntelligenceReportMapper reportMapper;
    private final MarketIntelligencePushLogMapper pushLogMapper;

    public DbMarketStore(
            MarketInfoSourceMapper sourceMapper,
            MarketKeywordTopicMapper topicMapper,
            MarketIntelligenceMapper itemMapper,
            MarketCrawlLogMapper crawlLogMapper,
            MarketIntelligenceVerifyLogMapper verifyLogMapper,
            MarketIntelligenceReportMapper reportMapper,
            MarketIntelligencePushLogMapper pushLogMapper
    ) {
        this.sourceMapper = sourceMapper;
        this.topicMapper = topicMapper;
        this.itemMapper = itemMapper;
        this.crawlLogMapper = crawlLogMapper;
        this.verifyLogMapper = verifyLogMapper;
        this.reportMapper = reportMapper;
        this.pushLogMapper = pushLogMapper;
    }

    @Override
    public MarketInfoSource saveSource(MarketInfoSource source) {
        OffsetDateTime now = OffsetDateTime.now();
        if (source.getId() == null) {
            source.setCreateTime(now);
            source.setUpdateTime(now);
            sourceMapper.insert(source);
        } else {
            source.setUpdateTime(now);
            sourceMapper.updateById(source);
        }
        return source;
    }

    @Override
    public MarketKeywordTopic saveTopic(MarketKeywordTopic topic) {
        OffsetDateTime now = OffsetDateTime.now();
        if (topic.getId() == null) {
            topic.setCreateTime(now);
            topic.setUpdateTime(now);
            topicMapper.insert(topic);
        } else {
            topic.setUpdateTime(now);
            topicMapper.updateById(topic);
        }
        return topic;
    }

    @Override
    public MarketIntelligence saveItem(MarketIntelligence item) {
        OffsetDateTime now = OffsetDateTime.now();
        if (item.getId() == null) {
            item.setCreateTime(now);
            item.setUpdateTime(now);
            itemMapper.insert(item);
        } else {
            item.setUpdateTime(now);
            itemMapper.updateById(item);
        }
        return item;
    }

    @Override
    public MarketCrawlLog saveCrawlLog(MarketCrawlLog log) {
        if (log.getId() == null) {
            log.setCreateTime(OffsetDateTime.now());
            crawlLogMapper.insert(log);
        } else {
            crawlLogMapper.updateById(log);
        }
        return log;
    }

    @Override
    public MarketIntelligenceVerifyLog saveVerifyLog(MarketIntelligenceVerifyLog log) {
        if (log.getId() == null) {
            log.setCreateTime(OffsetDateTime.now());
            verifyLogMapper.insert(log);
        } else {
            verifyLogMapper.updateById(log);
        }
        return log;
    }

    @Override
    public MarketIntelligenceReport saveReport(MarketIntelligenceReport report) {
        if (report.getId() == null) {
            report.setCreateTime(OffsetDateTime.now());
            reportMapper.insert(report);
        } else {
            reportMapper.updateById(report);
        }
        return report;
    }

    @Override
    public MarketIntelligencePushLog savePushLog(MarketIntelligencePushLog log) {
        if (log.getId() == null) {
            log.setCreateTime(OffsetDateTime.now());
            pushLogMapper.insert(log);
        } else {
            pushLogMapper.updateById(log);
        }
        return log;
    }

    @Override
    public List<MarketInfoSource> listSources() {
        return sourceMapper.selectList(new LambdaQueryWrapper<MarketInfoSource>()
                .eq(MarketInfoSource::isDeleted, false)
                .orderByDesc(MarketInfoSource::getCreateTime));
    }

    @Override
    public List<MarketKeywordTopic> listTopics() {
        return topicMapper.selectList(new LambdaQueryWrapper<MarketKeywordTopic>()
                .eq(MarketKeywordTopic::isDeleted, false)
                .orderByDesc(MarketKeywordTopic::getCreateTime));
    }

    @Override
    public List<MarketIntelligence> listItems() {
        return itemMapper.selectList(new LambdaQueryWrapper<MarketIntelligence>()
                .eq(MarketIntelligence::isDeleted, false)
                .orderByDesc(MarketIntelligence::getCreateTime));
    }

    @Override
    public List<MarketCrawlLog> listCrawlLogs() {
        return crawlLogMapper.selectList(new LambdaQueryWrapper<MarketCrawlLog>().orderByDesc(MarketCrawlLog::getStartTime));
    }

    @Override
    public List<MarketIntelligenceVerifyLog> listVerifyLogs() {
        return verifyLogMapper.selectList(new LambdaQueryWrapper<MarketIntelligenceVerifyLog>().orderByDesc(MarketIntelligenceVerifyLog::getCreateTime));
    }

    @Override
    public List<MarketIntelligencePushLog> listPushLogs() {
        return pushLogMapper.selectList(new LambdaQueryWrapper<MarketIntelligencePushLog>().orderByDesc(MarketIntelligencePushLog::getCreateTime));
    }

    @Override
    public List<MarketIntelligenceReport> listReports() {
        return reportMapper.selectList(new LambdaQueryWrapper<MarketIntelligenceReport>().orderByDesc(MarketIntelligenceReport::getReportTime));
    }

    @Override
    public Optional<MarketInfoSource> findSource(Long id) {
        return Optional.ofNullable(sourceMapper.selectById(id)).filter(source -> !source.isDeleted());
    }

    @Override
    public Optional<MarketInfoSource> findSourceByCode(String sourceCode) {
        return Optional.ofNullable(sourceMapper.selectOne(new LambdaQueryWrapper<MarketInfoSource>()
                .eq(MarketInfoSource::isDeleted, false)
                .eq(MarketInfoSource::getSourceCode, sourceCode)
                .last("limit 1")));
    }

    @Override
    public Optional<MarketKeywordTopic> findTopic(Long id) {
        return Optional.ofNullable(topicMapper.selectById(id)).filter(topic -> !topic.isDeleted());
    }

    @Override
    public Optional<MarketIntelligence> findItem(Long id) {
        return Optional.ofNullable(itemMapper.selectById(id)).filter(item -> !item.isDeleted());
    }

    @Override
    public Optional<MarketIntelligenceReport> findReport(Long id) {
        return Optional.ofNullable(reportMapper.selectById(id));
    }

    @Override
    public Optional<MarketCrawlLog> findCrawlLog(Long id) {
        return Optional.ofNullable(crawlLogMapper.selectById(id));
    }

    @Override
    public boolean hasDuplicate(String urlHash, String contentHash) {
        Long count = itemMapper.selectCount(new LambdaQueryWrapper<MarketIntelligence>()
                .eq(MarketIntelligence::isDeleted, false)
                .and(wrapper -> wrapper.eq(MarketIntelligence::getUrlHash, urlHash)
                        .or()
                        .eq(MarketIntelligence::getContentHash, contentHash)));
        return count != null && count > 0;
    }

    @Override
    public List<MarketIntelligenceVerifyLog> listVerifyLogs(Long itemId) {
        return verifyLogMapper.selectList(new LambdaQueryWrapper<MarketIntelligenceVerifyLog>()
                .eq(MarketIntelligenceVerifyLog::getIntelligenceId, itemId)
                .orderByDesc(MarketIntelligenceVerifyLog::getCreateTime));
    }

    @Override
    public List<MarketIntelligencePushLog> listPushLogs(Long itemId) {
        return pushLogMapper.selectList(new LambdaQueryWrapper<MarketIntelligencePushLog>()
                .eq(MarketIntelligencePushLog::getIntelligenceId, itemId)
                .orderByDesc(MarketIntelligencePushLog::getCreateTime));
    }
}
