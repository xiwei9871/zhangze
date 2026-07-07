package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.MatchScope;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketKeywordTopic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarketKeywordMatchServiceTest {

    private final MarketKeywordMatchService service = new MarketKeywordMatchService();

    @Test
    void matchesIncludeKeywordsAndFiltersExcludedKeywords() {
        MarketKeywordTopic topic = new MarketKeywordTopic();
        topic.setId(1L);
        topic.setTopicName("招标机会");
        topic.setIncludeKeywords(List.of("招标", "智慧工厂"));
        topic.setExcludeKeywords(List.of("废标"));
        topic.setMatchScope(MatchScope.TITLE_AND_CONTENT);
        topic.setImportanceLevel(ImportanceLevel.HIGH);
        topic.setEnabled(true);

        MarketKeywordMatchService.MatchResult result = service.match(
                "某某智慧工厂改造项目废标公告",
                "采购自动化监测设备及数据平台",
                List.of(topic)
        );

        assertThat(result.matched()).isFalse();
        assertThat(result.matchedTopicIds()).isEmpty();
        assertThat(result.matchedKeywords()).isEmpty();
    }

    @Test
    void keepsAllMatchedTopicsAndUsesHighestImportance() {
        MarketKeywordTopic tender = new MarketKeywordTopic();
        tender.setId(1L);
        tender.setTopicName("招标机会");
        tender.setIncludeKeywords(List.of("招标", "采购"));
        tender.setExcludeKeywords(List.of());
        tender.setMatchScope(MatchScope.TITLE_AND_CONTENT);
        tender.setImportanceLevel(ImportanceLevel.MEDIUM);
        tender.setEnabled(true);

        MarketKeywordTopic competitor = new MarketKeywordTopic();
        competitor.setId(2L);
        competitor.setTopicName("竞争对手动态");
        competitor.setIncludeKeywords(List.of("竞争公司"));
        competitor.setExcludeKeywords(List.of());
        competitor.setMatchScope(MatchScope.CONTENT);
        competitor.setImportanceLevel(ImportanceLevel.URGENT);
        competitor.setEnabled(true);

        MarketKeywordMatchService.MatchResult result = service.match(
                "轨道交通监测平台招标公告",
                "竞争公司已参与前期采购调研",
                List.of(tender, competitor)
        );

        assertThat(result.matched()).isTrue();
        assertThat(result.matchedTopicIds()).containsExactly(1L, 2L);
        assertThat(result.matchedKeywords()).containsExactlyInAnyOrder("招标", "采购", "竞争公司");
        assertThat(result.highestImportance()).isEqualTo(ImportanceLevel.URGENT);
    }
}
