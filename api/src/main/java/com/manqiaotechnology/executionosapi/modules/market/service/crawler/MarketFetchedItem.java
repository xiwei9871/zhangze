package com.manqiaotechnology.executionosapi.modules.market.service.crawler;

import java.time.OffsetDateTime;
import java.util.List;

public record MarketFetchedItem(
        String title,
        String url,
        OffsetDateTime publishTime,
        String author,
        String content,
        String html,
        List<String> tags
) {
}
