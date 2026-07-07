create table if not exists market_info_source (
    id bigserial primary key,
    source_name varchar(200) not null,
    source_code varchar(100) not null unique,
    source_type varchar(40) not null,
    url text not null,
    fetch_mode varchar(40) not null,
    fetch_interval_minutes integer not null default 60,
    enabled boolean not null default true,
    default_owner_user_id bigint,
    default_manager_user_id bigint,
    last_fetch_time timestamptz,
    last_fetch_status varchar(40),
    last_error_message text,
    remark text,
    create_time timestamptz not null default now(),
    update_time timestamptz not null default now(),
    create_by bigint,
    update_by bigint,
    deleted boolean not null default false
);

create table if not exists market_keyword_topic (
    id bigserial primary key,
    topic_name varchar(200) not null,
    include_keywords jsonb not null default '[]',
    exclude_keywords jsonb not null default '[]',
    match_scope varchar(40) not null,
    importance_level varchar(40) not null,
    owner_user_id bigint,
    manager_user_id bigint,
    enabled boolean not null default true,
    remark text,
    create_time timestamptz not null default now(),
    update_time timestamptz not null default now(),
    create_by bigint,
    update_by bigint,
    deleted boolean not null default false
);

create table if not exists market_intelligence (
    id bigserial primary key,
    source_id bigint references market_info_source(id),
    source_name varchar(200),
    source_type varchar(40),
    title text not null,
    url text not null,
    url_hash varchar(64) not null,
    title_hash varchar(64) not null,
    content_hash varchar(64) not null,
    publish_time timestamptz,
    crawl_time timestamptz not null default now(),
    raw_content text,
    content_excerpt text,
    matched_keywords jsonb not null default '[]',
    matched_topic_ids jsonb not null default '[]',
    ai_summary text,
    ai_key_points jsonb not null default '[]',
    ai_opportunity text,
    ai_risk text,
    ai_suggestion text,
    ai_related_companies jsonb not null default '[]',
    ai_related_regions jsonb not null default '[]',
    ai_competitors jsonb not null default '[]',
    ai_importance_score integer,
    ai_confidence integer,
    ai_error_message text,
    manual_importance_level varchar(40) not null,
    status varchar(40) not null,
    owner_user_id bigint,
    manager_user_id bigint,
    reported boolean not null default false,
    report_time timestamptz,
    verify_user_id bigint,
    verify_time timestamptz,
    verify_remark text,
    create_time timestamptz not null default now(),
    update_time timestamptz not null default now(),
    create_by bigint,
    update_by bigint,
    deleted boolean not null default false
);

create unique index if not exists uk_market_intelligence_url_hash on market_intelligence(url_hash) where deleted = false;
create index if not exists idx_market_intelligence_content_hash on market_intelligence(content_hash);
create index if not exists idx_market_intelligence_status on market_intelligence(status);

create table if not exists market_crawl_log (
    id bigserial primary key,
    source_id bigint,
    batch_no varchar(100) not null,
    start_time timestamptz not null,
    end_time timestamptz,
    status varchar(40) not null,
    fetched_count integer not null default 0,
    created_count integer not null default 0,
    duplicated_count integer not null default 0,
    matched_count integer not null default 0,
    failed_count integer not null default 0,
    error_message text,
    create_time timestamptz not null default now()
);

create table if not exists market_intelligence_verify_log (
    id bigserial primary key,
    intelligence_id bigint not null references market_intelligence(id),
    before_status varchar(40),
    after_status varchar(40) not null,
    importance_level varchar(40),
    verify_remark text,
    action_suggestion text,
    operator_id bigint,
    create_time timestamptz not null default now()
);

create table if not exists market_intelligence_report (
    id bigserial primary key,
    intelligence_id bigint not null references market_intelligence(id),
    report_title varchar(300) not null,
    report_content text not null,
    report_to_user_id bigint not null,
    report_by_user_id bigint not null,
    report_time timestamptz not null,
    manager_feedback text,
    create_time timestamptz not null default now()
);

create table if not exists market_intelligence_push_log (
    id bigserial primary key,
    intelligence_id bigint references market_intelligence(id),
    push_type varchar(40) not null,
    push_to_user_id bigint not null,
    push_title varchar(300) not null,
    push_content text,
    push_status varchar(40) not null,
    error_message text,
    create_time timestamptz not null default now()
);
