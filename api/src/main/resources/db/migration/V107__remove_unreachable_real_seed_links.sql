update market_intelligence
set deleted = true,
    update_time = now(),
    verify_remark = coalesce(verify_remark, '') || ' 已下线：原文链接经验证不可直接访问。'
where url in (
    'https://www.fzztb.com/moreInfoController.do?getNoticeDetail=&type=2&key=7BC7880FC4BF89309F44777372F9E546',
    'https://www.gddci.com/article/8931.html',
    'https://www.gzzb.gd.cn/cms/wz/view/other/jgy.jsp?infoId=1067776',
    'https://www.bdebid.com/ggzyjy_2025/ZFCG/ZC23/gkzb/202506/t20250606_45778.html'
)
  and deleted = false;
