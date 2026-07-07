package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.enums.IntelligenceStatus;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import org.springframework.stereotype.Service;

@Service
public class MarketAiSummaryService {
    private final MarketAiClient aiClient;

    public MarketAiSummaryService(MarketAiClient aiClient) {
        this.aiClient = aiClient;
    }

    public void summarize(MarketIntelligence item) {
        item.setStatus(IntelligenceStatus.PENDING_SUMMARY);
        try {
            MarketAiClient.AiSummary summary = aiClient.summarize(item);
            item.setAiSummary(summary.summary());
            item.setAiKeyPoints(summary.keyPoints());
            item.setAiOpportunity(summary.opportunity());
            item.setAiRisk(summary.risk());
            item.setAiSuggestion(summary.suggestion());
            item.setAiRelatedCompanies(summary.relatedCompanies());
            item.setAiRelatedRegions(summary.relatedRegions());
            item.setAiCompetitors(summary.competitors());
            item.setAiImportanceScore(summary.importanceScore());
            item.setAiConfidence(summary.confidence());
            item.setAiErrorMessage(null);
            item.setStatus(IntelligenceStatus.PENDING_VERIFY);
        } catch (RuntimeException ex) {
            item.setAiErrorMessage(ex.getMessage());
            item.setStatus(IntelligenceStatus.SUMMARY_FAILED);
        }
    }
}
