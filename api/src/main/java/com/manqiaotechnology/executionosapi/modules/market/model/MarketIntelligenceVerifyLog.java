package com.manqiaotechnology.executionosapi.modules.market.model;

import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.IntelligenceStatus;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MarketIntelligenceVerifyLog {
    private Long id;
    private Long intelligenceId;
    private IntelligenceStatus beforeStatus;
    private IntelligenceStatus afterStatus;
    private ImportanceLevel importanceLevel;
    private String verifyRemark;
    private String actionSuggestion;
    private Long operatorId;
    private OffsetDateTime createTime;
}
