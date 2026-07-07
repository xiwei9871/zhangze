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

    @Test
    void companyBusinessTopicsMatchGeologicalRisksWithoutGenericAiNews() {
        MarketKeywordTopic geologicalRisk = new MarketKeywordTopic();
        geologicalRisk.setId(10L);
        geologicalRisk.setTopicName("地质风险与微动探测");
        geologicalRisk.setIncludeKeywords(List.of("微动探测", "地质风险", "岩溶", "溶洞", "空洞", "沉降", "横波速度"));
        geologicalRisk.setExcludeKeywords(List.of("ai agent", "大模型", "融资"));
        geologicalRisk.setMatchScope(MatchScope.TITLE_AND_CONTENT);
        geologicalRisk.setImportanceLevel(ImportanceLevel.URGENT);
        geologicalRisk.setEnabled(true);

        MarketKeywordMatchService.MatchResult matched = service.match(
                "地铁盾构区间地质风险排查项目招标",
                "采用微动探测识别岩溶、空洞和沉降风险源",
                List.of(geologicalRisk)
        );
        MarketKeywordMatchService.MatchResult ignored = service.match(
                "AI Agent 公司完成新一轮融资",
                "大模型应用市场快速增长",
                List.of(geologicalRisk)
        );

        assertThat(matched.matched()).isTrue();
        assertThat(matched.matchedKeywords()).contains("微动探测", "地质风险", "岩溶", "空洞", "沉降");
        assertThat(ignored.matched()).isFalse();
    }

    @Test
    void compoundKeywordsRequireEveryPartToMatch() {
        MarketKeywordTopic upstream = new MarketKeywordTopic();
        upstream.setId(20L);
        upstream.setTopicName("基础设施建设进展前置信号");
        upstream.setIncludeKeywords(List.of(
                "地铁+中标",
                "地铁+施工",
                "地铁+车站封顶",
                "地铁+盾构洞通",
                "地铁+盾构始发",
                "地铁+区间贯通",
                "高铁+施工总承包",
                "高铁+开工",
                "铁路+路基+中标",
                "机场+改扩建+中标",
                "机场+跑道+施工",
                "输电线路+中标",
                "高压铁塔+开工",
                "电力铁塔+基础施工"
        ));
        upstream.setExcludeKeywords(List.of("客流", "航班", "票价"));
        upstream.setMatchScope(MatchScope.TITLE_AND_CONTENT);
        upstream.setImportanceLevel(ImportanceLevel.HIGH);
        upstream.setEnabled(true);

        MarketKeywordMatchService.MatchResult metroConstruction = service.match(
                "成都轨道交通某地铁区间施工总承包公告",
                "项目包含盾构区间和车站土建工程",
                List.of(upstream)
        );
        MarketKeywordMatchService.MatchResult metroMilestone = service.match(
                "某地铁车站封顶，盾构区间贯通",
                "后续进入附属结构施工阶段",
                List.of(upstream)
        );
        MarketKeywordMatchService.MatchResult airportConstruction = service.match(
                "机场跑道改扩建工程施工公告",
                "飞行区基础设施建设进入实施阶段",
                List.of(upstream)
        );
        MarketKeywordMatchService.MatchResult powerTower = service.match(
                "高压铁塔基础施工项目开工",
                "输电线路通道同步开展建设",
                List.of(upstream)
        );
        MarketKeywordMatchService.MatchResult ignoredBySingleWord = service.match(
                "地铁客流恢复明显",
                "运营服务持续优化",
                List.of(upstream)
        );
        MarketKeywordMatchService.MatchResult ignoredByExcludedWord = service.match(
                "机场改扩建中标公告",
                "航班保障服务采购",
                List.of(upstream)
        );

        assertThat(metroConstruction.matched()).isTrue();
        assertThat(metroConstruction.matchedKeywords()).contains("地铁+施工");
        assertThat(metroMilestone.matched()).isTrue();
        assertThat(metroMilestone.matchedKeywords()).contains("地铁+车站封顶", "地铁+区间贯通");
        assertThat(airportConstruction.matched()).isTrue();
        assertThat(airportConstruction.matchedKeywords()).contains("机场+跑道+施工");
        assertThat(powerTower.matched()).isTrue();
        assertThat(powerTower.matchedKeywords()).contains("高压铁塔+开工");
        assertThat(ignoredBySingleWord.matched()).isFalse();
        assertThat(ignoredByExcludedWord.matched()).isFalse();
    }
}
