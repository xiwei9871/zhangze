#!/usr/bin/env bash
set -euo pipefail

API_BASE="${API_BASE:-http://127.0.0.1:8080/api/v1/market/intelligence}"

URLS=(
  "https://zfcg.henan.gov.cn/hkgq/content?infoId=1900673"
  "https://ggzyfw.beijing.gov.cn/jyxxgcjshtgs/20250314/5000991.html"
  "https://www.wutos.com/news/291.html"
  "https://www.wuxi.gov.cn/doc/2026/01/12/4715658.shtml"
  "https://jtys.sz.gov.cn/gkmlpt/content/12/12663/post_12663974.html"
)

for url in "${URLS[@]}"; do
  curl -fsSL --max-time 15 --range 0-4096 -A "Mozilla/5.0" -o /dev/null "${url}"
done

curl -sS -X POST "${API_BASE}/sources" \
  -H 'Content-Type: application/json' \
  -d '{
    "sourceName": "真实网页导入源",
    "sourceCode": "real_public_web_import",
    "sourceType": "TENDER",
    "url": "https://www.gov.cn/",
    "fetchMode": "EXTERNAL_IMPORT",
    "fetchIntervalMinutes": 1440,
    "enabled": true,
    "defaultOwnerUserId": 1007,
    "defaultManagerUserId": 2007,
    "fetchConfig": {},
    "remark": "真实公开网页导入源，用于承载招投标、建设进展和行业公开网页的原文链接"
  }' >/dev/null || true

curl -sS -X POST "${API_BASE}/external/items/import" \
  -H 'Content-Type: application/json' \
  --data @- <<'JSON'
{
  "sourceCode": "real_public_web_import",
  "items": [
    {
      "title": "郑州航空港2025年度市政道路空洞点雷达检测服务项目公开招标公告",
      "url": "https://zfcg.henan.gov.cn/hkgq/content?infoId=1900673",
      "publishTime": "2025-12-23T16:25:00+08:00",
      "author": "郑州航空港实验区政府采购网",
      "content": "郑州航空港经济综合实验区生态环境和城市管理局2025年度市政道路空洞点雷达检测服务项目公开招标公告。项目包含市政道路空洞点雷达检测，命中道路病害、地下空洞、地质雷达和路面沉降等业务方向，可作为道路病害排查商机线索。",
      "html": "",
      "tags": ["招标公告", "道路雷达", "道路病害", "地质雷达"]
    },
    {
      "title": "2025年北京市管城市道路雷达空洞检测项目第5标段合同公示",
      "url": "https://ggzyfw.beijing.gov.cn/jyxxgcjshtgs/20250314/5000991.html",
      "publishTime": "2025-03-14T09:00:00+08:00",
      "author": "北京市公共资源交易服务平台",
      "content": "2025年北京市管城市道路雷达空洞检测项目第5标段合同公示，公告内容包含中标人、合同金额、合同签署日期和附件。该信息命中道路雷达、道路空洞检测、道路病害排查等业务方向，可用于跟踪同类项目中标和客户需求。",
      "html": "",
      "tags": ["合同公示", "道路雷达", "空洞检测", "中标信息"]
    },
    {
      "title": "高压输电线路动态稳定监测预警系统设备采购招标公告",
      "url": "https://www.wutos.com/news/291.html",
      "publishTime": "2023-11-03T09:00:00+08:00",
      "author": "武汉理工光科股份有限公司",
      "content": "高压输电线路动态稳定监测预警系统设备采购招标公告。该公告涉及高压输电线路动态稳定监测、预警系统设备采购和在线监测业务方向，可作为电力高压铁塔、输电线路在线监测相关商机线索。",
      "html": "",
      "tags": ["招标公告", "高压输电线路", "在线监测", "预警系统"]
    },
    {
      "title": "无锡轨交四线共建一线通车全速开局",
      "url": "https://www.wuxi.gov.cn/doc/2026/01/12/4715658.shtml",
      "publishTime": "2026-01-12T08:02:00+08:00",
      "author": "无锡市人民政府",
      "content": "无锡轨交建设四线共建一线通车全速开局。新闻提到地铁5号线盾构区间右线顺利始发、地铁6号线车站主体结构完成封顶、S2线大盾构即将运抵等进展，命中地铁施工、盾构始发、车站封顶和轨道交通建设前置信号。",
      "html": "",
      "tags": ["轨道交通", "地铁施工", "盾构始发", "车站封顶"]
    },
    {
      "title": "深圳地铁27号线首批两台盾构始发",
      "url": "https://jtys.sz.gov.cn/gkmlpt/content/12/12663/post_12663974.html",
      "publishTime": "2026-03-10T09:00:00+08:00",
      "author": "深圳市交通运输局",
      "content": "深圳地铁27号线首批两台盾构始发。该公开信息属于地铁建设施工进展，命中地铁施工、盾构始发、轨道交通和地下结构等前置信号，可用于判断后续地质风险排查、沉降监测和地下结构检测机会。",
      "html": "",
      "tags": ["地铁", "盾构始发", "轨道交通", "施工进展"]
    }
  ]
}
JSON

echo "Imported real public business items into ${API_BASE}"
