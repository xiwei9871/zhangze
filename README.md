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

## 设计边界

- HTML 解析使用 jsoup，RSS 解析预留 ROME，调度互斥使用本地 `AtomicBoolean` 并预留 ShedLock 依赖。
- 首轮 AI 使用 `MockMarketAiClient`，迁回 Execution OS 时替换为现有 `modules/ai`。
- 首轮通知落入 `market_intelligence_push_log`，迁回 Execution OS 时替换为系统内通知能力。
- 不做验证码破解、登录绕过或反爬对抗；复杂网站通过外部导入接口接入。
