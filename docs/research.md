# 可复用工具调研

## 后端

- `jsoup`：用于 HTML 请求、解析、正文清洗和基础选择器抽取。
- `ROME`：用于 RSS/Atom 源解析。
- `ShedLock`：迁回多实例部署时用于定时任务分布式锁。
- `Resilience4j`：用于后续对抓取请求增加重试、限流和熔断。
- `Testcontainers PostgreSQL`：用于 Flyway 和 Mapper 集成测试。

## 前端

- `Element Plus`：表格、表单、抽屉、弹窗、标签和消息提示。
- `ECharts`：看板趋势图、来源分布图和排行图。
- `Axios`：统一请求封装，对齐 Execution OS `@/utils/request` 习惯。

## 不纳入首轮

- 登录绕过、验证码破解、强反爬对抗。
- 复杂相似度聚类。首轮使用 URL、标题、正文 hash 去重。
- 独立外部推送平台。首轮只记录推送日志。
