package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.IntelligenceStatus;
import com.manqiaotechnology.executionosapi.modules.market.enums.MatchScope;
import com.manqiaotechnology.executionosapi.modules.market.enums.SourceType;
import com.manqiaotechnology.executionosapi.modules.market.model.ExternalImportRequest;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketKeywordTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarketWorkflowServiceTest {

    private MarketWorkflowService workflow;
    private InMemoryMarketStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryMarketStore();
        workflow = new MarketWorkflowService(
                store,
                new MarketKeywordMatchService(),
                new MarketDeduplicateService(),
                new MarketAiSummaryService(new MockMarketAiClient()),
                new MarketNotificationService(store)
        );

        MarketInfoSource source = new MarketInfoSource();
        source.setSourceName("示例招采站");
        source.setSourceCode("tender_site_a");
        source.setSourceType(SourceType.TENDER);
        source.setFetchMode(FetchMode.EXTERNAL_IMPORT);
        source.setUrl("https://example.com/tender");
        source.setEnabled(true);
        source.setDefaultOwnerUserId(101L);
        source.setDefaultManagerUserId(201L);
        store.saveSource(source);

        MarketKeywordTopic topic = new MarketKeywordTopic();
        topic.setTopicName("招标机会");
        topic.setIncludeKeywords(List.of("招标", "采购", "智慧工厂"));
        topic.setExcludeKeywords(List.of("废标"));
        topic.setMatchScope(MatchScope.TITLE_AND_CONTENT);
        topic.setImportanceLevel(ImportanceLevel.HIGH);
        topic.setOwnerUserId(102L);
        topic.setManagerUserId(202L);
        topic.setEnabled(true);
        store.saveTopic(topic);
    }

    @Test
    void externalImportCreatesSummarizedPendingVerifyItemAndSkipsDuplicateUrl() {
        ExternalImportRequest request = new ExternalImportRequest("tender_site_a", List.of(
                new ExternalImportRequest.Item(
                        "某某智慧工厂改造项目招标公告",
                        "https://example.com/detail/123",
                        OffsetDateTime.parse("2026-07-07T10:00:00+08:00"),
                        "来源站点",
                        "正文包含采购自动化监测设备及数据平台",
                        "<html>正文包含采购自动化监测设备及数据平台</html>",
                        List.of("招标", "轨道交通")
                ),
                new ExternalImportRequest.Item(
                        "某某智慧工厂改造项目招标公告",
                        "https://example.com/detail/123",
                        OffsetDateTime.parse("2026-07-07T10:00:00+08:00"),
                        "来源站点",
                        "重复正文包含采购自动化监测设备及数据平台",
                        null,
                        List.of("招标")
                )
        ));

        MarketWorkflowService.ImportResult result = workflow.importExternalItems(request);

        assertThat(result.createdCount()).isEqualTo(1);
        assertThat(result.duplicatedCount()).isEqualTo(1);
        assertThat(result.matchedCount()).isEqualTo(1);

        MarketIntelligence item = store.listItems().get(0);
        assertThat(item.getStatus()).isEqualTo(IntelligenceStatus.PENDING_VERIFY);
        assertThat(item.getAiSummary()).contains("智慧工厂");
        assertThat(item.getOwnerUserId()).isEqualTo(102L);
        assertThat(store.listPushLogs()).hasSize(1);
    }

    @Test
    void aiFailureKeepsItemAndAllowsResummarize() {
        ExternalImportRequest request = new ExternalImportRequest("tender_site_a", List.of(
                new ExternalImportRequest.Item(
                        "智慧工厂招标 AI_FAIL",
                        "https://example.com/detail/fail",
                        OffsetDateTime.parse("2026-07-07T10:00:00+08:00"),
                        "来源站点",
                        "采购数据平台 AI_FAIL",
                        null,
                        List.of()
                )
        ));

        workflow.importExternalItems(request);
        MarketIntelligence item = store.listItems().get(0);

        assertThat(item.getStatus()).isEqualTo(IntelligenceStatus.SUMMARY_FAILED);
        assertThat(item.getAiErrorMessage()).contains("mock ai failure");

        item.setRawContent("采购数据平台，摘要重试内容已修复");
        workflow.resummarize(item.getId());

        assertThat(item.getStatus()).isEqualTo(IntelligenceStatus.PENDING_VERIFY);
        assertThat(item.getAiSummary()).contains("摘要重试内容");
    }

    @Test
    void verifyAndReportCreateTraceableLogs() {
        ExternalImportRequest request = new ExternalImportRequest("tender_site_a", List.of(
                new ExternalImportRequest.Item(
                        "智慧工厂招标公告",
                        "https://example.com/detail/report",
                        OffsetDateTime.parse("2026-07-07T10:00:00+08:00"),
                        "来源站点",
                        "采购自动化监测设备及数据平台",
                        null,
                        List.of()
                )
        ));

        workflow.importExternalItems(request);
        Long itemId = store.listItems().get(0).getId();

        workflow.verify(itemId, IntelligenceStatus.FOLLOW_UP, ImportanceLevel.URGENT, "值得跟进", "当天核验投标条件", 901L);
        workflow.report(itemId, "重要招标机会", "请管理层关注", 202L, 901L);

        MarketIntelligence item = store.findItem(itemId).orElseThrow();
        assertThat(item.getStatus()).isEqualTo(IntelligenceStatus.REPORTED);
        assertThat(item.isReported()).isTrue();
        assertThat(store.listVerifyLogs()).hasSize(1);
        assertThat(store.listReports()).hasSize(1);
        assertThat(store.listPushLogs()).hasSize(2);
    }
}
