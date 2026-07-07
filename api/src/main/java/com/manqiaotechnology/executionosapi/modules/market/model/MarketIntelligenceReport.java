package com.manqiaotechnology.executionosapi.modules.market.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("market_intelligence_report")
public class MarketIntelligenceReport {
    @TableId(type = IdType.AUTO)
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
