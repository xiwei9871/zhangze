#!/usr/bin/env bash
set -euo pipefail

curl -sS -X POST http://localhost:8080/api/v1/market/intelligence/external/items/import \
  -H 'Content-Type: application/json' \
  -d '{
    "sourceCode": "tender_site_a",
    "items": [
      {
        "title": "某某智慧工厂改造项目招标公告",
        "url": "https://example.com/detail/123",
        "publishTime": "2026-07-07T10:00:00+08:00",
        "author": "来源站点",
        "content": "正文包含采购自动化监测设备及数据平台",
        "html": null,
        "tags": ["招标", "轨道交通"]
      }
    ]
  }'
