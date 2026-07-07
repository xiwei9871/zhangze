#!/usr/bin/env bash
set -euo pipefail

API_BASE="${API_BASE:-http://127.0.0.1:8080/api/v1/market/intelligence}"

curl -sS -X POST "${API_BASE}/sources" \
  -H 'Content-Type: application/json' \
  -d '{
    "sourceName": "36氪 RSS",
    "sourceCode": "real_36kr_rss",
    "sourceType": "NEWS",
    "url": "https://36kr.com/feed",
    "fetchMode": "RSS",
    "fetchIntervalMinutes": 60,
    "enabled": true,
    "defaultOwnerUserId": 1001,
    "defaultManagerUserId": 2001,
    "fetchConfig": {},
    "remark": "真实 RSS 数据源，用于完整路线验证"
  }' >/dev/null || true

curl -sS -X POST "${API_BASE}/topics" \
  -H 'Content-Type: application/json' \
  -d '{
    "topicName": "地质风险与微动探测",
    "includeKeywords": ["微动探测", "微动勘探", "地质风险", "地质隐患", "岩溶", "溶洞", "孤石", "空洞", "脱层", "地面塌陷", "地面沉降", "滞后沉降", "采空区", "横波速度", "地质CT"],
    "excludeKeywords": ["AI Agent", "大模型", "芯片", "证券", "股票", "消费电子", "娱乐", "广告"],
    "matchScope": "TITLE_AND_CONTENT",
    "importanceLevel": "URGENT",
    "ownerUserId": 1001,
    "managerUserId": 2001,
    "enabled": true,
    "remark": "幔壳主营业务：地质风险排查和微动探测"
  }' >/dev/null || true

curl -sS -X POST "${API_BASE}/topics" \
  -H 'Content-Type: application/json' \
  -d '{
    "topicName": "道路病害与地质雷达",
    "includeKeywords": ["道路病害", "道路塌陷", "路面塌陷", "路面沉降", "道路空洞", "地下空洞", "地质雷达", "探地雷达", "GPR", "路基脱空", "路基沉降", "路面检测", "病害体"],
    "excludeKeywords": ["消费", "汽车销量", "广告", "证券", "股票"],
    "matchScope": "TITLE_AND_CONTENT",
    "importanceLevel": "HIGH",
    "ownerUserId": 1002,
    "managerUserId": 2002,
    "enabled": true,
    "remark": "幔壳主营业务：道路病害排查和地质雷达"
  }' >/dev/null || true

curl -sS -X POST "${API_BASE}/topics" \
  -H 'Content-Type: application/json' \
  -d '{
    "topicName": "工厂数字化与智能化改造",
    "includeKeywords": ["工厂数字化", "智能化改造", "智慧工厂", "智能制造", "生产线改造", "自动化产线", "设备在线监测", "工业互联网", "数字孪生", "三维建模", "数字化管理平台", "监测大数据平台", "MES", "SCADA"],
    "excludeKeywords": ["手机", "游戏", "消费电子", "广告"],
    "matchScope": "TITLE_AND_CONTENT",
    "importanceLevel": "HIGH",
    "ownerUserId": 1005,
    "managerUserId": 2005,
    "enabled": true,
    "remark": "幔壳主营业务：工厂数字化和智能化改造"
  }' >/dev/null || true

curl -sS -X POST "${API_BASE}/topics" \
  -H 'Content-Type: application/json' \
  -d '{
    "topicName": "电力高压铁塔在线监测",
    "includeKeywords": ["高压铁塔", "输电铁塔", "电力铁塔", "高压线铁塔", "铁塔基础", "塔基沉降", "塔基位移", "输电线路在线监测", "电力通道地质风险", "杆塔基础", "高压电桩", "边坡滑坡监测"],
    "excludeKeywords": ["股价", "证券", "广告"],
    "matchScope": "TITLE_AND_CONTENT",
    "importanceLevel": "HIGH",
    "ownerUserId": 1006,
    "managerUserId": 2006,
    "enabled": true,
    "remark": "幔壳主营业务：高压铁塔和输电线路在线监测"
  }' >/dev/null || true

curl -sS -X POST "${API_BASE}/topics" \
  -H 'Content-Type: application/json' \
  -d '{
    "topicName": "基础设施建设进展前置信号",
    "includeKeywords": [
      "地铁+中标", "地铁+施工", "地铁+土建", "地铁+开工", "地铁+车站封顶", "地铁+主体结构封顶",
      "地铁+盾构始发", "地铁+盾构接收", "地铁+盾构贯通", "地铁+盾构洞通", "地铁+区间贯通", "地铁+铺轨",
      "轨道交通+中标", "轨道交通+施工", "轨道交通+土建", "轨道交通+开工", "轨道交通+车站封顶",
      "轨道交通+盾构始发", "轨道交通+盾构接收", "轨道交通+盾构贯通", "轨道交通+区间贯通",
      "高铁+中标", "高铁+施工", "高铁+开工", "高铁+站房+中标", "高铁+路基+施工",
      "铁路+中标", "铁路+施工", "铁路+开工", "铁路+路基+中标", "铁路+路基+施工", "铁路+隧道+贯通",
      "机场+中标", "机场+施工", "机场+改扩建", "机场+跑道+施工", "机场+跑道+中标", "机场+飞行区+施工", "机场+停机坪+施工",
      "输电线路+中标", "输电线路+施工", "输电线路+开工", "高压铁塔+中标", "高压铁塔+施工", "高压铁塔+开工", "电力铁塔+基础施工", "铁塔基础+施工", "杆塔基础+施工"
    ],
    "excludeKeywords": ["客流", "票价", "运营调整", "航班", "招聘", "股票", "证券", "广告", "文旅", "消费"],
    "matchScope": "TITLE_AND_CONTENT",
    "importanceLevel": "HIGH",
    "ownerUserId": 1007,
    "managerUserId": 2007,
    "enabled": true,
    "remark": "上游建设进展信号，使用 + 表示多个词必须同时出现"
  }' >/dev/null || true

echo "Seeded real RSS source and ManQiao business topics at ${API_BASE}"
