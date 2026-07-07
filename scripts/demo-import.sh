#!/usr/bin/env bash
set -euo pipefail

curl -sS -X POST http://localhost:8080/api/v1/market/intelligence/external/items/import \
  -H 'Content-Type: application/json' \
  -d '{
    "sourceCode": "real_public_web_import",
    "items": [
      {
        "title": "郑州航空港2025年度市政道路空洞点雷达检测服务项目公开招标公告",
        "url": "https://zfcg.henan.gov.cn/hkgq/content?infoId=1900673",
        "publishTime": "2025-12-23T16:25:00+08:00",
        "author": "郑州航空港实验区政府采购网",
        "content": "郑州航空港经济综合实验区生态环境和城市管理局2025年度市政道路空洞点雷达检测服务项目公开招标公告。项目包含市政道路空洞点雷达检测，命中道路病害、地下空洞、地质雷达和路面沉降等业务方向。",
        "html": null,
        "tags": ["招标公告", "道路雷达", "道路病害", "地质雷达"]
      }
    ]
  }'
