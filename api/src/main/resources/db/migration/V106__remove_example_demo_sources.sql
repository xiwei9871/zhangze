update market_intelligence
set deleted = true,
    update_time = now()
where url like 'https://example.com/%'
  and deleted = false;

update market_info_source
set enabled = false,
    deleted = true,
    remark = coalesce(remark, '') || ' 已下线：早期演示占位源，避免真实路线中出现 example.com 链接。',
    update_time = now()
where source_code in (
    'news_public_demo',
    'tender_site_a',
    'industry_demo',
    'competitor_demo',
    'external_spider_demo',
    'e2e_business_import'
)
  and deleted = false;
