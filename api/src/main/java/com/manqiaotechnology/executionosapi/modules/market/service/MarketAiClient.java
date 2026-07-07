package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;

public interface MarketAiClient {
    AiSummary summarize(MarketIntelligence item);

    record AiSummary(
            String summary,
            java.util.List<String> keyPoints,
            String opportunity,
            String risk,
            String suggestion,
            java.util.List<String> relatedCompanies,
            java.util.List<String> relatedRegions,
            java.util.List<String> competitors,
            Integer importanceScore,
            Integer confidence
    ) {
    }
}
