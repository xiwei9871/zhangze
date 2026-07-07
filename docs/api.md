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
}
```
