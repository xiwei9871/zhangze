insert into market_info_source (source_name, source_code, source_type, url, fetch_mode, default_owner_user_id, default_manager_user_id)
values
('公开新闻栏目', 'news_public_demo', 'NEWS', 'https://example.com/news', 'HTML', 101, 201),
('招采公开栏目', 'tender_site_a', 'TENDER', 'https://example.com/tender', 'EXTERNAL_IMPORT', 102, 202),
('行业资讯栏目', 'industry_demo', 'INDUSTRY_DATA', 'https://example.com/industry', 'RSS', 103, 203),
('竞争公司官网新闻', 'competitor_demo', 'COMPETITOR', 'https://example.com/competitor', 'HTML', 104, 204),
('外部爬虫导入源', 'external_spider_demo', 'OTHER', 'https://example.com/import', 'EXTERNAL_IMPORT', 105, 205)
on conflict (source_code) do nothing;

insert into market_keyword_topic (topic_name, include_keywords, exclude_keywords, match_scope, importance_level, owner_user_id, manager_user_id)
values
('招标机会', '["招标","采购","项目建设","改造","扩建"]', '["废标","终止"]', 'TITLE_AND_CONTENT', 'HIGH', 102, 202),
('竞争对手动态', '["竞争公司名称","中标","发布","合作"]', '[]', 'TITLE_AND_CONTENT', 'HIGH', 104, 204),
('重点客户项目', '["重点客户名称","数据平台","智慧工厂"]', '[]', 'TITLE_AND_CONTENT', 'URGENT', 101, 201),
('政策变化', '["政策","办法","通知","规划"]', '[]', 'TITLE_AND_CONTENT', 'MEDIUM', 103, 203),
('行业市场变化', '["价格","市场","需求","产能"]', '[]', 'TITLE_AND_CONTENT', 'MEDIUM', 105, 205);
