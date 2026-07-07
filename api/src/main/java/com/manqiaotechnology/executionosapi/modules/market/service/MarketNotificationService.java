package com.manqiaotechnology.executionosapi.modules.market.service;

import com.manqiaotechnology.executionosapi.modules.market.enums.PushStatus;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligence;
import com.manqiaotechnology.executionosapi.modules.market.model.MarketIntelligencePushLog;
import org.springframework.stereotype.Service;

@Service
public class MarketNotificationService {
    private final InMemoryMarketStore store;

    public MarketNotificationService(InMemoryMarketStore store) {
        this.store = store;
    }

    public void pushToOwner(MarketIntelligence item, String title) {
        push(item, "OWNER", item.getOwnerUserId(), title);
    }

    public void pushToManager(MarketIntelligence item, Long managerUserId, String title) {
        push(item, "MANAGER", managerUserId, title);
    }

    private void push(MarketIntelligence item, String type, Long userId, String title) {
        if (userId == null) {
            return;
        }
        MarketIntelligencePushLog log = new MarketIntelligencePushLog();
        log.setIntelligenceId(item.getId());
        log.setPushType(type);
        log.setPushToUserId(userId);
        log.setPushTitle(title);
        log.setPushContent(item.getTitle() + "\n" + item.getAiSummary() + "\n" + item.getUrl());
        log.setPushStatus(PushStatus.SUCCESS);
        store.savePushLog(log);
    }
}
