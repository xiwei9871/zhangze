package com.manqiaotechnology.executionosapi.modules.market.service.crawler;

import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RssFeedFetcher implements MarketSourceFetcher {
    @Override
    public boolean supports(FetchMode fetchMode) {
        return fetchMode == FetchMode.RSS;
    }

    @Override
    public List<MarketFetchedItem> fetch(MarketInfoSource source) throws Exception {
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(source.getUrl())));
        List<MarketFetchedItem> items = new ArrayList<>();
        for (SyndEntry entry : feed.getEntries()) {
            String link = firstNonBlank(entry.getLink(), entry.getUri());
            String content = contentOf(entry);
            items.add(new MarketFetchedItem(
                    clean(entry.getTitle()),
                    clean(link),
                    toOffsetDateTime(firstNonNull(entry.getPublishedDate(), entry.getUpdatedDate())),
                    clean(entry.getAuthor()),
                    content,
                    null,
                    List.of()
            ));
        }
        return items;
    }

    private static String contentOf(SyndEntry entry) {
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            return cleanHtml(entry.getContents().stream().map(SyndContent::getValue).filter(value -> value != null).findFirst().orElse(""));
        }
        if (entry.getDescription() != null) {
            return cleanHtml(entry.getDescription().getValue());
        }
        return "";
    }

    private static OffsetDateTime toOffsetDateTime(Date date) {
        return date == null ? null : date.toInstant().atOffset(ZoneOffset.UTC);
    }

    private static Date firstNonNull(Date left, Date right) {
        return left != null ? left : right;
    }

    private static String firstNonBlank(String left, String right) {
        return left != null && !left.isBlank() ? left : right;
    }

    private static String cleanHtml(String value) {
        return value == null ? "" : Jsoup.parse(value).text().trim();
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
