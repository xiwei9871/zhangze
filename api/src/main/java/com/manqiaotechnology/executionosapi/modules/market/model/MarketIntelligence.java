package com.manqiaotechnology.executionosapi.modules.market.model;

import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.IntelligenceStatus;
import com.manqiaotechnology.executionosapi.modules.market.enums.SourceType;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MarketIntelligence {
    private Long id;
    private Long sourceId;
    private String sourceName;
    private SourceType sourceType = SourceType.OTHER;
    private String title;
    private String url;
    private String urlHash;
    private String titleHash;
    private String contentHash;
    private OffsetDateTime publishTime;
    private OffsetDateTime crawlTime;
    private String rawContent;
    private String contentExcerpt;
    private List<String> matchedKeywords = new ArrayList<>();
    private List<Long> matchedTopicIds = new ArrayList<>();
    private String aiSummary;
    private List<String> aiKeyPoints = new ArrayList<>();
    private String aiOpportunity;
    private String aiRisk;
    private String aiSuggestion;
    private List<String> aiRelatedCompanies = new ArrayList<>();
    private List<String> aiRelatedRegions = new ArrayList<>();
    private List<String> aiCompetitors = new ArrayList<>();
    private Integer aiImportanceScore;
    private Integer aiConfidence;
    private String aiErrorMessage;
    private ImportanceLevel manualImportanceLevel = ImportanceLevel.MEDIUM;
    private IntelligenceStatus status = IntelligenceStatus.PENDING_SUMMARY;
    private Long ownerUserId;
    private Long managerUserId;
    private boolean reported;
    private OffsetDateTime reportTime;
    private Long verifyUserId;
    private OffsetDateTime verifyTime;
    private String verifyRemark;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private boolean deleted;
}
