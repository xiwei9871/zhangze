update market_keyword_topic
set enabled = false,
    remark = coalesce(remark, '') || ' 已停用：宽泛市场主题，改由幔壳主营业务主题替代。',
    update_time = now()
where topic_name in ('真实市场动态', '行业市场变化')
  and deleted = false;

do $$
begin
    if not exists (select 1 from market_keyword_topic where topic_name = '地质风险与微动探测' and deleted = false) then
        insert into market_keyword_topic (topic_name, include_keywords, exclude_keywords, match_scope, importance_level, owner_user_id, manager_user_id, remark)
        values (
            '地质风险与微动探测',
            '["微动探测","微动勘探","地质风险","地质隐患","地质灾害","不良地质体","岩溶","溶洞","孤石","空洞","脱层","地层松散","地面塌陷","地面沉降","滞后沉降","采空区","砂层透镜体","横波速度","瑞雷面波","地质CT","地下空间"]',
            '["AI Agent","大模型","芯片","证券","股票","消费电子","娱乐","广告"]',
            'TITLE_AND_CONTENT',
            'URGENT',
            1001,
            2001,
            '来源：幔壳宣传册与微动探测工程案例。'
        );
    end if;

    if not exists (select 1 from market_keyword_topic where topic_name = '道路病害与地质雷达' and deleted = false) then
        insert into market_keyword_topic (topic_name, include_keywords, exclude_keywords, match_scope, importance_level, owner_user_id, manager_user_id, remark)
        values (
            '道路病害与地质雷达',
            '["道路病害","道路塌陷","路面塌陷","路面沉降","道路空洞","地下空洞","地质雷达","探地雷达","GPR","路基脱空","路基沉降","路面检测","病害体","道路体检","地下病害"]',
            '["消费","汽车销量","广告","证券","股票"]',
            'TITLE_AND_CONTENT',
            'HIGH',
            1002,
            2002,
            '道路病害排查和地质雷达相关机会。'
        );
    end if;

    if not exists (select 1 from market_keyword_topic where topic_name = '轨道交通铁路机场基础设施风险' and deleted = false) then
        insert into market_keyword_topic (topic_name, include_keywords, exclude_keywords, match_scope, importance_level, owner_user_id, manager_user_id, remark)
        values (
            '轨道交通铁路机场基础设施风险',
            '["地铁","盾构","隧道","轨道交通","高铁","铁路路基","机场跑道","停机坪","滑行道","管廊","边坡","坍塌","塌陷","沉降监测","注浆加固","注浆效果","地层结构","路基安全"]',
            '["票价","客流","文旅","广告"]',
            'TITLE_AND_CONTENT',
            'HIGH',
            1003,
            2003,
            '面向地铁、高铁、机场、隧道、管廊等基础设施风险排查。'
        );
    end if;

    if not exists (select 1 from market_keyword_topic where topic_name = '地下结构与无损检测' and deleted = false) then
        insert into market_keyword_topic (topic_name, include_keywords, exclude_keywords, match_scope, importance_level, owner_user_id, manager_user_id, remark)
        values (
            '地下结构与无损检测',
            '["地下结构探测","地下建构筑物","地下管线","管线探测","结构检测","无损检测","SAFT","超声波","面波检测","红外热成像","混凝土缺陷","钢筋结构","渗漏水探测","涵洞探测","基础结构探测"]',
            '["医疗超声","消费电子","广告"]',
            'TITLE_AND_CONTENT',
            'MEDIUM',
            1004,
            2004,
            '地下人工建构筑物、管线、混凝土结构和无损检测相关。'
        );
    end if;

    if not exists (select 1 from market_keyword_topic where topic_name = '工厂数字化与智能化改造' and deleted = false) then
        insert into market_keyword_topic (topic_name, include_keywords, exclude_keywords, match_scope, importance_level, owner_user_id, manager_user_id, remark)
        values (
            '工厂数字化与智能化改造',
            '["工厂数字化","智能化改造","智慧工厂","智能制造","生产线改造","自动化产线","设备在线监测","工业互联网","数字孪生","三维建模","数字化管理平台","监测大数据平台","MES","SCADA"]',
            '["手机","游戏","消费电子","广告"]',
            'TITLE_AND_CONTENT',
            'HIGH',
            1005,
            2005,
            '工厂数字化、智能化改造、三维建模和监测平台相关。'
        );
    end if;

    if not exists (select 1 from market_keyword_topic where topic_name = '电力高压铁塔在线监测' and deleted = false) then
        insert into market_keyword_topic (topic_name, include_keywords, exclude_keywords, match_scope, importance_level, owner_user_id, manager_user_id, remark)
        values (
            '电力高压铁塔在线监测',
            '["高压铁塔","输电铁塔","电力铁塔","高压线铁塔","铁塔基础","塔基沉降","塔基位移","输电线路在线监测","电力通道地质风险","杆塔基础","高压电桩","边坡滑坡监测","输电线路监测"]',
            '["股价","证券","广告"]',
            'TITLE_AND_CONTENT',
            'HIGH',
            1006,
            2006,
            '电力高压铁塔、输电线路和塔基风险在线监测相关。'
        );
    end if;
end $$;
