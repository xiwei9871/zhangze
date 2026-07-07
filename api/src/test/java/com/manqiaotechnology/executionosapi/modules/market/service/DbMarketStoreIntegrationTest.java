package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.enums.SourceType;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class DbMarketStoreIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("market_intelligence")
            .withUsername("market")
            .withPassword("market");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MarketStore store;

    @Test
    void persistsSourceFetchConfigAndDetectsItemDuplicates() {
        MarketInfoSource source = new MarketInfoSource();
        source.setSourceName("测试 RSS");
        source.setSourceCode("test_rss");
        source.setSourceType(SourceType.NEWS);
        source.setFetchMode(FetchMode.RSS);
        source.setUrl("https://example.com/feed.xml");
        source.setFetchConfig(Map.of("itemSelector", ".item"));

        MarketInfoSource saved = store.saveSource(source);

        assertThat(store.findSource(saved.getId())).get()
                .extracting(MarketInfoSource::getFetchConfig)
                .satisfies(config -> assertThat(config).containsEntry("itemSelector", ".item"));

        MarketIntelligence item = new MarketIntelligence();
        item.setSourceId(saved.getId());
        item.setSourceName(saved.getSourceName());
        item.setSourceType(saved.getSourceType());
        item.setTitle("智慧工厂采购公告");
        item.setUrl("https://example.com/a");
        item.setUrlHash("url-hash-a");
        item.setTitleHash("title-hash-a");
        item.setContentHash("content-hash-a");
        item.setRawContent("采购监测设备");
        item.setContentExcerpt("采购监测设备");
        item.setMatchedKeywords(List.of("采购"));
        item.setMatchedTopicIds(List.of(1L));
        store.saveItem(item);

        assertThat(store.hasDuplicate("url-hash-a", "other-content")).isTrue();
        assertThat(store.hasDuplicate("other-url", "content-hash-a")).isTrue();
        assertThat(store.listItems()).hasSize(1);
    }
}
