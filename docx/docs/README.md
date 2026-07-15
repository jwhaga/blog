# Aurora — 前后端分离博客（重构版）

> 本仓库是 Aurora 博客项目的 **JDK 25 + Spring Boot 3.x 重构版**。
> 原始项目位于 [`../aurora/`](../aurora/) 目录，保持只读作为对比基线。

---

## 技术栈（重构后）

| 层级 | 技术 | 原始版本 | 重构版本 |
|------|------|----------|----------|
| JDK | — | 1.8 | **25** (Temurin) |
| 框架 | Spring Boot | 2.3.7.RELEASE | **3.5.16** |
| 安全 | Spring Security | 5.x | **6.x** (SecurityFilterChain + JWT) |
| ORM | MyBatis-Plus | 3.4.2 | **3.5.9** |
| 搜索 | Elasticsearch | 7.9.2 | **8.15.3** + IK |
| 文档 | Knife4j | 2.0.7 | **4.5.0** (OpenAPI 3) |
| JWT | jjwt | 0.9.0 | **0.12.6** |
| 测试 | — | 0 | **56 个单元测试** |

> 完整技术栈清单见 [技术栈.md](技术栈.md)。

## 快速开始

### 前置条件

- JDK 25 + Maven 3.9+
- Docker Desktop（MySQL、Redis、RabbitMQ、ES）
- Node.js 24（前端）
- 详见 [本地运行环境清单.md](本地运行环境清单.md)

### 启动基础设施

```powershell
# 一键启动 MySQL、Redis、RabbitMQ、Elasticsearch
docker compose up -d

# 确认所有服务健康
docker ps
```

### 编译与测试

```powershell
$env:JAVA_HOME = "D:\Develop\JDK25\jdk-25.0.3+9"

# 编译
mvn clean compile -f aurora-springboot\pom.xml -q

# 运行 56 个单元测试
mvn clean test -f aurora-springboot\pom.xml

# 打包
mvn clean package -f aurora-springboot\pom.xml -DskipTests -q
```

### 启动后端

```powershell
D:\Develop\JDK25\jdk-25.0.3+9\bin\java.exe -jar aurora-springboot\target\aurora-springboot-0.0.1.jar --spring.profiles.active=dev --server.port=8081
```

### 登录

| 用户名 | 密码 | 说明 |
|--------|------|------|
| `admin@163.com` | `admin` | 管理员（t_user_auth 表 id=1） |

## 项目结构

```
重构版/
├── aurora-springboot/      # 后端（Maven + Spring Boot 3.5.16 + JDK 25）
│   ├── src/main/java/      # 源代码
│   ├── src/main/resources/ # 配置与 SQL
│   └── pom.xml
├── aurora-vue/             # 前端（Vue 3 前台 + Vue 2 后台）
├── .github/workflows/      # CI/CD（GitHub Actions）
├── docker-compose.yml      # 基础设施编排
├── Dockerfile.es           # ES 8.15.3 + IK 构建
└── 文档                     # 重构方案、技术栈、环境清单等
```

## 重构进度

| 阶段 | 状态 |
|------|------|
| Phase 1：后端核心框架升级 (Part 1.1~1.8) | ✅ M1 达成（后端可编译启动） |
| Phase 2：后端代码治理 (Part 2.1~2.6) | ✅ M2 达成（代码治理完成） |
| Phase 4：基础设施与工程化 (Part 4.1~4.4) | ✅ 进行中 |
| Phase 3：前端重构 (Part 3.1~3.6) | ⏳ 待开始 |
| Phase 5：收尾与发布 | ⏳ 待开始 |

## 相关文档

| 文档 | 说明 |
|------|------|
| [技术栈.md](技术栈.md) | 完整技术清单及版本 |
| [本地运行环境清单.md](本地运行环境清单.md) | 本地开发环境搭建指南 |
| [重构方案.md](重构方案.md) | 重构总体方案与任务分解 |
| [测试策略.md](测试策略.md) | 测试分层策略与计划 |
| [代码规范.md](代码规范.md) | 编码规范与 Git 提交规范 |
| [交接文档.md](交接文档.md) | 重构进度交接（供 AI 会话恢复上下文） |

## 开源许可

基于原始项目 [aurora](https://github.com/linhaojun123/aurora) 重构，遵循原始项目开源协议。
