package com.manqiaotechnology.executionosapi.modules.market.model;

import com.manqiaotechnology.executionosapi.modules.market.enums.PushStatus;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MarketIntelligencePushLog {
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
