package com.manqiaotechnology.executionosapi.modules.market.service.crawler;

import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HtmlSelectorFetcher implements MarketSourceFetcher {
    @Override
    public boolean supports(FetchMode fetchMode) {
        return fetchMode == FetchMode.HTML || fetchMode == FetchMode.SEARCH_PAGE;
    }

    @Override
    public List<MarketFetchedItem> fetch(MarketInfoSource source) throws Exception {
        Document document = Jsoup.connect(source.getUrl())
                .userAgent("MarketIntelligenceMonitor/0.1")
                .timeout(15_000)
                .get();
        Map<String, Object> config = source.getFetchConfig() == null ? Map.of() : source.getFetchConfig();
        String itemSelector = stringConfig(config, "itemSelector", "article");
        String titleSelector = stringConfig(config, "titleSelector", "a");
        String linkSelector = stringConfig(config, "linkSelector", titleSelector);
        String contentSelector = stringConfig(config, "contentSelector", "");
        String publishTimeSelector = stringConfig(config, "publishTimeSelector", "");

        List<MarketFetchedItem> items = new ArrayList<>();
        for (Element itemElement : document.select(itemSelector)) {
            Element titleElement = first(itemElement, titleSelector);
            Element linkElement = first(itemElement, linkSelector);
            String title = titleElement == null ? itemElement.text() : titleElement.text();
            String url = linkElement == null ? source.getUrl() : linkElement.absUrl("href");
            String content = contentSelector.isBlank() ? itemElement.text() : textOf(itemElement, contentSelector);
            OffsetDateTime publishTime = parsePublishTime(first(itemElement, publishTimeSelector));
            if (!title.isBlank() && !url.isBlank()) {
                items.add(new MarketFetchedItem(title.trim(), url.trim(), publishTime, null, content.trim(), itemElement.outerHtml(), List.of()));
            }
        }
        return items;
    }

    private static String textOf(Element root, String selector) {
        Element element = first(root, selector);
        return element == null ? "" : element.text();
    }

    private static Element first(Element root, String selector) {
        if (selector == null || selector.isBlank()) {
            return null;
        }
        return root.selectFirst(selector);
    }

    private static OffsetDateTime parsePublishTime(Element element) {
        if (element == null) {
            return null;
        }
        String raw = firstText(element.attr("datetime"), element.attr("content"), element.text());
        if (raw.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw);
        } catch (RuntimeException ignored) {
            try {
                return LocalDate.parse(raw).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
            } catch (RuntimeException ignoredAgain) {
                return null;
            }
        }
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private static String stringConfig(Map<String, Object> config, String key, String defaultValue) {
        Object value = config.get(key);
        return value == null ? defaultValue : value.toString();
    }
}
