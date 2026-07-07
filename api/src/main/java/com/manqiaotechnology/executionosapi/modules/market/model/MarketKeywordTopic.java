package com.manqiaotechnology.executionosapi.modules.market.model;

import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.MatchScope;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MarketKeywordTopic {
    private Long id;
    private String topicName;
    private List<String> includeKeywords = new ArrayList<>();
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
