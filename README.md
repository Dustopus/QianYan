# 🌟 千言（QianYan）

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen.svg)
![LangChain4j](https://img.shields.io/badge/LangChain4j-1.1.0-blue.svg)
![License](https://img.shields.io/badge/License-AGPL--3.0-yellow.svg)

**一个基于 Java + LangChain4j 构建的企业级智能体系统**

[功能特性](#-核心功能) · [快速开始](#-快速开始) · [架构设计](#-系统架构) · [部署指南](#-部署)

</div>

---

## 📖 项目简介

**千言（QianYan）** 不仅仅是一个简单的问答机器人。它通过深度整合 **RAG（检索增强生成）**、**MCP（模型上下文协议）**、**分布式记忆** 与**工具调用**，构建出具备感知、记忆、规划与行动能力的企业级智能体系统。

旨在解决企业内部：
- 🏝️ **知识孤岛**：各部门知识分散，难以统一检索利用
- ⚙️ **自动化程度低**：重复性工作耗费大量人力

---

## ✨ 核心功能

| 功能模块 | 说明 |
|---------|------|
| 🔍 **RAG 动态知识植入** | 基于 PgVector 的向量检索系统，支持文档语义切片与高维向量化存储，实现私有领域知识精准检索 |
| 🧠 **分布式会话记忆** | 基于 Redis 的分布式对话记忆系统，支持多用户并发会话隔离和跨服务实例长程多轮对话 |
| 🛠️ **智能任务编排** | 利用 Function Calling 实现查天气、发邮件等工具调用，支持 MCP 协议接入外部搜索 |
| 🛡️ **安全护轨机制** | 基于 LangChain4j Guardrail 的合规拦截，实现敏感词过滤与 Prompt 注入检测 |
| 📊 **全链路监控** | 集成 Prometheus + Grafana，对 Token 消耗、调用频次等核心指标实时可视化监控 |
| 💬 **流式输出** | 支持 Flux 流式响应，提供实时逐字输出体验 |

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────┐
│                    前端 (HTML)                       │
├─────────────────────────────────────────────────────┤
│                  Spring Boot API                     │
│  ┌──────────┐  ┌──────────┐  ┌───────────────────┐  │
│  │ AI Chat  │  │ REST API │  │  Actuator Monitor │  │
│  │ Service  │  │Controller│  │  (Prometheus)     │  │
│  └────┬─────┘  └──────────┘  └───────────────────┘  │
│       │                                             │
│  ┌────┴──────────────────────────────────────────┐  │
│  │              LangChain4j Core                 │  │
│  │  ┌─────────┐ ┌──────────┐ ┌───────────────┐  │  │
│  │  │AI Model │ │  Tools   │ │  Guardrails   │  │  │
│  │  │(Qwen)   │ │(Time,Mail│ │  (Input Safe) │  │  │
│  │  │         │ │ RAG, MCP)│ │               │  │  │
│  │  └─────────┘ └──────────┘ └───────────────┘  │  │
│  └───────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────┤
│              数据层                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │ PgVector │  │  Redis   │  │  File System     │  │
│  │(向量库)  │  │(会话记忆)│  │  (文档存储)      │  │
│  └──────────┘  └──────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────┘
```

---

## 🚀 快速开始

### 环境要求

- **JDK 17+**
- **PostgreSQL** (需安装 pgvector 扩展)
- **Redis**
- **DashScope API Key** (阿里灵积，用于 Qwen 模型)
- **BigModel API Key** (智谱，用于 MCP 联网搜索)

### 1. 克隆项目

```bash
git clone https://github.com/Dustopus/QianYan.git
cd QianYan
```

### 2. 配置数据库

```sql
-- PostgreSQL: 创建数据库并启用 pgvector
CREATE DATABASE qianyan;
\c qianyan
CREATE EXTENSION vector;
```

### 3. 修改配置

编辑 `src/main/resources/application-dev.yml`：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your-redis-password
      ttl: 3600

pgvector:
  database: qianyan
  host: localhost
  port: 5432
  user: postgres
  password: your-pgvector-password
  table: agent_embedding
```

编辑 `src/main/resources/application.yml`：

```yaml
langchain4j:
  community:
    dashscope:
      chat-model:
        api-key: your-dashscope-api-key
      streaming-chat-model:
        api-key: your-dashscope-api-key
      embedding-model:
        api-key: your-dashscope-api-key

bigmodel:
  api-key: your-bigmodel-api-key
```

### 4. 启动服务

```bash
mvn spring-boot:run
```

服务默认运行在 `http://localhost:10010/api`

---

## 📡 API 接口

### 普通对话

```bash
POST /api/ai/chat
Content-Type: application/json

{
  "memoryId": 123456,
  "message": "你好，介绍一下你自己",
  "userId": 1001,
  "sessionId": 123456
}
```

### 流式对话 (SSE)

```bash
POST /api/ai/streamChat
Content-Type: application/json

{
  "memoryId": 123456,
  "message": "写一首关于春天的诗",
  "userId": 1001,
  "sessionId": 123456
}
```

### 知识植入

```bash
POST /api/insert
Content-Type: application/json

{
  "question": "这个软件叫什么名字？",
  "answer": "本软件名为「千言」...",
  "sourceName": "QianYan.md"
}
```

### 监控端点

```bash
GET /api/actuator/prometheus   # Prometheus 指标
GET /api/actuator/health       # 健康检查
```

---

## 🔧 技术栈

| 类别 | 技术 |
|------|------|
| **后端框架** | Spring Boot 3.5.9 |
| **AI 框架** | LangChain4j 1.1.0 |
| **大语言模型** | 通义千问 (Qwen-Max via DashScope) |
| **向量数据库** | PostgreSQL + pgvector |
| **缓存/记忆** | Redis |
| **嵌入模型** | text-embedding-v4 (DashScope) |
| **外部搜索** | 智谱 MCP (BigModel) |
| **邮件服务** | Spring Mail |
| **监控** | Prometheus + Grafana |

---

## 🐳 部署

### Docker 部署

```bash
# 构建镜像
docker build -t qianyan:latest .

# 运行容器
docker run -d \
  --name qianyan \
  -p 10010:10010 \
  -e SPRING_PROFILES_ACTIVE=prod \
  qianyan:latest
```

### 监控面板

配合 `src/main/resources/system-prompt/prometheus.yml` 配置 Prometheus + Grafana，实现可视化监控。

---

## 📁 项目结构

```
QianYan/
├── src/main/java/com/dustopus/qianyan/
│   ├── QianYanApplication.java          # 启动类
│   ├── ai/                              # AI 核心服务
│   │   ├── AiChat.java                  # AI 接口定义
│   │   └── AiChatService.java           # AI 服务配置
│   ├── config/                          # 配置类
│   │   ├── DashScopeModelConfig.java    # 大模型配置
│   │   ├── EmbeddingStoreConfig.java    # 向量库配置
│   │   ├── McpToolConfig.java           # MCP 工具配置
│   │   └── RagConfig.java               # RAG 引擎配置
│   ├── controller/                      # 控制器层
│   ├── tool/                            # 工具类 (Time, Email, RAG)
│   ├── Monitor/                         # 监控指标收集
│   ├── guardrail/                       # 安全护栏
│   └── Exception/                       # 全局异常处理
├── src/main/resources/
│   ├── application.yml                  # 主配置
│   ├── application-dev.yml              # 开发环境配置
│   ├── docs/                            # RAG 知识文档
│   ├── front/                           # 前端页面
│   └── system-prompt/                   # 系统提示词
└── pom.xml                              # Maven 依赖
```

---

## 📄 许可证

[AGPL-3.0 License](LICENSE)

---

<div align="center">
  Made with ❤️ by <a href="https://github.com/Dustopus">Dustopus</a>
</div>
