package com.manqiaotechnology.executionosapi.modules.market.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manqiaotechnology.executionosapi.modules.market.enums.PushStatus;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("market_intelligence_push_log")
public class MarketIntelligencePushLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long intelligenceId;
    private String pushType;
    private Long pushToUserId;
    private String pushTitle;
    private String pushContent;
    private PushStatus pushStatus = PushStatus.PENDING;
    private String errorMessage;
    private OffsetDateTime createTime;
}
