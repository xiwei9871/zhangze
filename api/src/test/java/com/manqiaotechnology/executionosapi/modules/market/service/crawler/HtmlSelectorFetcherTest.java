package com.manqiaotechnology.executionosapi.modules.market.service.crawler;

import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlSelectorFetcherTest {
    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void fetchesItemsWithConfiguredSelectors() throws Exception {
        String html = """
                <!doctype html>
                <html>
                  <body>
                    <article class="news-card">
                      <a class="title" href="/detail/1">新能源设备招标公告</a>
                      <time datetime="2026-07-07T09:30:00+08:00">2026-07-07</time>
                      <p class="summary">采购储能生产线监测系统。</p>
                    </article>
                    <article class="news-card">
                      <a class="title" href="https://example.com/detail/2">行业政策更新</a>
                      <p class="summary">产业扶持方向。</p>
                    </article>
                  </body>
                </html>
                """;
        server = startServer(html, "text/html");

        MarketInfoSource source = new MarketInfoSource();
        source.setUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/list");
        source.setFetchMode(FetchMode.HTML);
        source.setFetchConfig(Map.of(
                "itemSelector", ".news-card",
                "titleSelector", ".title",
                "linkSelector", ".title",
                "contentSelector", ".summary",
                "publishTimeSelector", "time"
        ));

        List<MarketFetchedItem> items = new HtmlSelectorFetcher().fetch(source);

        assertThat(items).hasSize(2);
        assertThat(items.get(0).title()).isEqualTo("新能源设备招标公告");
        assertThat(items.get(0).url()).startsWith("http://127.0.0.1:");
        assertThat(items.get(0).url()).endsWith("/detail/1");
        assertThat(items.get(0).content()).contains("采购储能生产线监测系统");
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
