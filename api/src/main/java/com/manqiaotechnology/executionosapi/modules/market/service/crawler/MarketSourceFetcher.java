package com.manqiaotechnology.executionosapi.modules.market.service.crawler;

import com.manqiaotechnology.executionosapi.modules.market.enums.FetchMode;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketInfoSource;

import java.util.List;

public interface MarketSourceFetcher {
    boolean supports(FetchMode fetchMode);

    List<MarketFetchedItem> fetch(MarketInfoSource source) throws Exception;
}
