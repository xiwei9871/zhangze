package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.enums.ImportanceLevel;
import com.manqiaotechnology.executionosapi.modules.market.enums.MatchScope;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketKeywordTopic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class MarketKeywordMatchService {

    public MatchResult match(String title, String content, List<MarketKeywordTopic> topics) {
        Set<String> matchedKeywords = new LinkedHashSet<>();
        List<Long> matchedTopicIds = new ArrayList<>();
        ImportanceLevel highest = null;

        String safeTitle = normalize(title);
        String safeContent = normalize(content);
        for (MarketKeywordTopic topic : topics) {
            if (!topic.isEnabled() || topic.isDeleted()) {
                continue;
            }
            String matchTarget = targetForScope(topic.getMatchScope(), safeTitle, safeContent);
            boolean excluded = topic.getExcludeKeywords().stream()
                    .filter(keyword -> !normalize(keyword).isBlank())
                    .anyMatch(keyword -> matchTarget.contains(normalize(keyword)));
            if (excluded) {
                continue;
            }
            List<String> hits = topic.getIncludeKeywords().stream()
                    .filter(keyword -> !normalize(keyword).isBlank())
                    .filter(keyword -> matchTarget.contains(normalize(keyword)))
                    .toList();
            if (!hits.isEmpty()) {
                matchedTopicIds.add(topic.getId());
                matchedKeywords.addAll(hits);
                highest = ImportanceLevel.max(highest, topic.getImportanceLevel());
            }
        }

        return new MatchResult(
                !matchedTopicIds.isEmpty(),
                new ArrayList<>(matchedKeywords),
                matchedTopicIds,
                highest == null ? ImportanceLevel.LOW : highest
        );
    }

    private static String targetForScope(MatchScope scope, String title, String content) {
        return switch (scope == null ? MatchScope.TITLE_AND_CONTENT : scope) {
            case TITLE -> title;
            case CONTENT -> content;
            case TITLE_AND_CONTENT -> title + "\n" + content;
        };
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    public record MatchResult(
            boolean matched,
            List<String> matchedKeywords,
            List<Long> matchedTopicIds,
            ImportanceLevel highestImportance
    ) {
    }
}
