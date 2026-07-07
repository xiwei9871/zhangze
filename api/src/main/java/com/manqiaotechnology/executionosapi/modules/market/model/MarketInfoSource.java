package com.manqiaotechnology.executionosapi.modules.market.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.enums.SourceType;
import com.manqiaotechnology.executionosapi.modules.market.persistence.JsonMapTypeHandler;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@TableName(value = "market_info_source", autoResultMap = true)
public class MarketInfoSource {
    @TableId(type = IdType.AUTO)
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
    @TableField(typeHandler = JsonMapTypeHandler.class)
    private Map<String, Object> fetchConfig = new LinkedHashMap<>();
    private String lastCrawlCursor;
    private String remark;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private boolean deleted;
}
