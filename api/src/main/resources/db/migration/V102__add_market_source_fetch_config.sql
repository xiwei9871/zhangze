alter table market_info_source
    add column if not exists fetch_config jsonb not null default '{}',
    add column if not exists last_crawl_cursor text;
