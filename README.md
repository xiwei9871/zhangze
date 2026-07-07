# 市场情报监测新仓库

本仓库用于开发“市场情报监测主线”的独立 MVP，并保持目录、接口和技术栈与 Execution OS 可迁入模块一致。

## 目录

- `api/`：Spring Boot 后端，接口前缀 `/api/v1/market/intelligence`
- `web/`：Vue 3 + Element Plus 前端
- `docs/`：调研、接口、验收和接入说明
- `scripts/`：本地辅助脚本

## 本地启动

后端需要 Java 17、Maven 和 PostgreSQL。

```bash
./scripts/setup-postgres.sh
cd api
mvn test
mvn spring-boot:run
```

前端需要 Node.js。

```bash
cd web
npm install
npm run build
npm run dev
```

真实数据路线：

```bash
./scripts/seed-real-route.sh
curl -X POST http://127.0.0.1:8080/api/v1/market/intelligence/crawl/run
./scripts/import-real-business-items.sh
```

默认会创建 `36氪 RSS` 信息源、幔壳主营业务关键词主题，并导入一批已做 HTTP 可访问性检查的真实公开网页链接。前端打开 `http://127.0.0.1:5173/` 后可查看真实抓取、可打开原文链接、去重、mock AI 摘要、核验、上报和看板统计。

也可以在后端和前端已启动后运行一键路线：

```bash
./scripts/run-real-route.sh
```

关键词主题已按幔壳主营业务收敛到地质风险/微动探测、道路病害/地质雷达、地下结构/无损检测、工厂数字化/智能化改造、电力高压铁塔在线监测等方向，详见 `docs/manqiao-business-keywords.md`。

## 设计边界

- HTML 解析使用 jsoup，RSS 解析使用 ROME，调度互斥使用本地 `AtomicBoolean` 并预留 ShedLock 依赖。
- 首轮 AI 使用 `MockMarketAiClient`，迁回 Execution OS 时替换为现有 `modules/ai`。
- 首轮通知落入 `market_intelligence_push_log`，迁回 Execution OS 时替换为系统内通知能力。
- 不做验证码破解、登录绕过或反爬对抗；复杂网站通过外部导入接口接入。
