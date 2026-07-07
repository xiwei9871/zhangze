package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockMarketAiClient implements MarketAiClient {

    @Override
    public AiSummary summarize(MarketIntelligence item) {
        String content = item.getRawContent() == null ? "" : item.getRawContent();
        if (content.contains("AI_FAIL")) {
            throw new IllegalStateException("mock ai failure: content requested simulated failure");
        }
        String excerpt = content.length() > 60 ? content.substring(0, 60) : content;
        return new AiSummary(
                item.getTitle() + "：" + excerpt,
                List.of("命中关键词：" + String.join("、", item.getMatchedKeywords()), "原文链接已保留"),
                "建议经营人员核验项目范围、预算和资格条件。",
                "公告信息可能不完整，需要查看原文和附件。",
                "当天完成原文核验，并判断是否进入商机跟进。",
                List.of(),
                List.of(),
                List.of(),
                Math.max(1, item.getManualImportanceLevel().rank()),
                86
        );
    }
}
