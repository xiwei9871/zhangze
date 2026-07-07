package com.manqiaotechnology.executionosapi.modules.market.model;

import com.manqiaotechnology.executionosapi.modules.market.enums.CrawlStatus;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MarketCrawlLog {
    private Long id;
    private Long sourceId;
    private String batchNo;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private CrawlStatus status = CrawlStatus.RUNNING;
    private int fetchedCount;
    private int createdCount;
    private int duplicatedCount;
    private int matchedCount;
    private int failedCount;
    private String errorMessage;
    private OffsetDateTime createTime;
}
