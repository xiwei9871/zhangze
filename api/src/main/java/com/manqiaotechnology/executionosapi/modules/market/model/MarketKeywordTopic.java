package com.manqiaotechnology.executionosapi.modules.market.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.MatchScope;
import com.manqiaotechnology.executionosapi.modules.market.persistence.JsonStringListTypeHandler;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName(value = "market_keyword_topic", autoResultMap = true)
public class MarketKeywordTopic {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String topicName;
    @TableField(typeHandler = JsonStringListTypeHandler.class)
    private List<String> includeKeywords = new ArrayList<>();
    @TableField(typeHandler = JsonStringListTypeHandler.class)
    private List<String> excludeKeywords = new ArrayList<>();
    private MatchScope matchScope = MatchScope.TITLE_AND_CONTENT;
    private ImportanceLevel importanceLevel = ImportanceLevel.MEDIUM;
    private Long ownerUserId;
    private Long managerUserId;
    private boolean enabled = true;
    private String remark;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private boolean deleted;
}
