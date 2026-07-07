package com.manqiaotechnology.executionosapi.modules.market.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MarketIntelligenceReport {
    private Long id;
    private Long intelligenceId;
    private String reportTitle;
    private String reportContent;
    private Long reportToUserId;
    private Long reportByUserId;
    private OffsetDateTime reportTime;
    private String managerFeedback;
    private OffsetDateTime createTime;
}
