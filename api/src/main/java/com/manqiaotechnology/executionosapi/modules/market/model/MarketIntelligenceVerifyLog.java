package com.manqiaotechnology.executionosapi.modules.market.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.IntelligenceStatus;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("market_intelligence_verify_log")
public class MarketIntelligenceVerifyLog {
    @TableId(type = IdType.AUTO)
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
