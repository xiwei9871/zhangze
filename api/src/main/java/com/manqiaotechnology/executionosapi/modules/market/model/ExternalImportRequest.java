package com.manqiaotechnology.executionosapi.modules.market.model;

import java.time.OffsetDateTime;
import java.util.List;

public record ExternalImportRequest(String sourceCode, List<Item> items) {
    public record Item(
            String title,
            String url,
            OffsetDateTime publishTime,
            String author,
            String content,
            String html,
            List<String> tags
    ) {
    }
}
