update market_keyword_topic
set enabled = false,
    remark = coalesce(remark, '') || ' 已停用：示例主题过宽，避免非主营业务信息误报。',
    update_time = now()
where topic_name in ('招标机会', '竞争对手动态', '重点客户项目', '政策变化')
  and deleted = false;
