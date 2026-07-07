# API 说明

所有接口前缀为 `/api/v1/market/intelligence`，返回结构为 `Result<T>` 或 `PageResult<T>`。

## 主要接口

- 信息源：`GET/POST /sources`、`GET/PUT/DELETE /sources/{id}`、`POST /sources/{id}/enable`、`POST /sources/{id}/disable`、`POST /sources/{id}/test-fetch`
- 关键词主题：`GET/POST /topics`、`GET/PUT/DELETE /topics/{id}`、`POST /topics/{id}/enable`、`POST /topics/{id}/disable`、`POST /topics/import`、`POST /topics/preview-match`
- 抓取：`POST /crawl/run`、`POST /crawl/run/{sourceId}`、`GET /crawl/logs`、`GET /crawl/logs/{id}`
- 情报：`GET /items`、`GET /items/{id}`、`POST /items/{id}/resummarize`、`POST /items/{id}/assign`、`POST /items/{id}/verify`
- 上报：`POST /items/{id}/report`、`GET /reports`、`GET /reports/{id}`
- 看板：`GET /dashboard/summary`、`GET /dashboard/trend`、`GET /dashboard/source-distribution`、`GET /dashboard/topic-rank`、`GET /dashboard/keyword-rank`
- 外部导入：`POST /external/items/import`

## 外部导入示例

```json
{
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
}
```
