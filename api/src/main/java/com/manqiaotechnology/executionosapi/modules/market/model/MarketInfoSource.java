package com.manqiaotechnology.executionosapi.modules.market.model;

import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.enums.SourceType;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MarketInfoSource {
    private Long id;
    private String sourceName;
    private String sourceCode;
    private SourceType sourceType = SourceType.OTHER;
    private String url;
    private FetchMode fetchMode = FetchMode.HTML;
    private Integer fetchIntervalMinutes = 60;
    private boolean enabled = true;
    private Long defaultOwnerUserId;
    private Long defaultManagerUserId;
    private OffsetDateTime lastFetchTime;
    private String lastFetchStatus;
    private String lastErrorMessage;
    private String remark;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private boolean deleted;
}
