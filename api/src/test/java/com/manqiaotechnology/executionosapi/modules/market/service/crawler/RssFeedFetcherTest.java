package com.manqiaotechnology.executionosapi.modules.market.service.crawler;

import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RssFeedFetcherTest {
    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void fetchesItemsFromRssFeed() throws Exception {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rss version="2.0">
                  <channel>
                    <title>Industry News</title>
                    <item>
                      <title>智慧工厂采购项目启动</title>
                      <link>https://example.com/news/1</link>
                      <pubDate>Tue, 07 Jul 2026 10:00:00 GMT</pubDate>
                      <description><![CDATA[采购自动化监测设备和数据平台。]]></description>
                    </item>
                    <item>
                      <title>无关新闻</title>
                      <link>https://example.com/news/2</link>
                      <description>其他内容</description>
                    </item>
                  </channel>
                </rss>
                """;
        server = startServer(xml, "application/rss+xml");

        MarketInfoSource source = new MarketInfoSource();
        source.setUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/feed.xml");
        source.setFetchMode(FetchMode.RSS);

        List<MarketFetchedItem> items = new RssFeedFetcher().fetch(source);

        assertThat(items).hasSize(2);
        assertThat(items.get(0).title()).isEqualTo("智慧工厂采购项目启动");
        assertThat(items.get(0).url()).isEqualTo("https://example.com/news/1");
        assertThat(items.get(0).content()).contains("采购自动化监测设备");
        assertThat(items.get(0).publishTime()).isNotNull();
    }

    private static HttpServer startServer(String body, String contentType) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        return server;
    }
}
