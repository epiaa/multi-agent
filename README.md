# Multi-Agent

基于 Spring Boot + LangChain4j 的多智能体系统，实现课程订阅与知识问答功能。

## 项目架构

```
┌────────────────────────────────────────────────────────────────┐
│                      用户请求                                    │
└───────────────────────────┬────────────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────────────┐
│                    SupervisorAgent (调度Agent)                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  1. 分析用户意图                                          │   │
│  │  2. 决定路由目标Agent                                     │   │
│  │  3. 返回JSON决策: {agent, task, reason}                  │   │
│  └─────────────────────────────────────────────────────────┘   │
└───────────────────────────┬────────────────────────────────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             ▼             ▼
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│  KnowledgeAgent  │ │   PublishAgent   │ │ SupervisorAgent  │
│   (知识问答)      │ │   (课程订阅)      │ │   (自己处理)      │
│                  │ │                  │ │                  │
│  • RAG检索       │ │  • Tool调用      │ │  • 简单对话       │
│  • PgVector存储  │ │  • 数据库操作     │ │  • 功能介绍       │
│  • 向量相似搜索   │ │  • 订阅管理       │ │  • 帮助说明       │
└──────────────────┘ └──────────────────┘ └──────────────────┘
```

## 核心组件

### Agent调度流程

1. **AgentRegistry** - Agent注册中心，自动收集所有Agent实现
2. **AgentDispatcher** - 调度器，解析Supervisor决策并分发任务
3. **AgentRouteDecision** - 路由决策DTO，包含agent、task、reason

### 调度示例

```
用户: "帮我订阅Java课程"
     ↓
Supervisor分析意图
     ↓
返回决策: {"agent": "publish-agent", "task": "订阅Java课程", "reason": "涉及课程订阅"}
     ↓
AgentDispatcher解析并路由到PublishAgent
     ↓
PublishAgent调用Tool执行订阅操作
     ↓
返回结果给用户
```

## 技术栈

- **框架**: Spring Boot 3.2.7 + Java 17
- **AI框架**: LangChain4j 1.0.1-beta6
- **LLM**: 阿里云通义千问 (qwen3-max)
- **向量数据库**: PostgreSQL + pgvector
- **缓存**: Redis (会话存储)
- **ORM**: MyBatis-Plus

## 功能模块

| Agent | 功能 | 技术实现 |
|-------|------|----------|
| SupervisorAgent | 意图分析、任务路由 | SystemMessage + JSON决策 |
| KnowledgeAgent | 课程知识问答 | RAG + PgVector检索 |
| PublishAgent | 课程订阅管理 | Tool + 数据库操作 |

## 环境配置

### 必需的环境变量

```bash
# OpenAI API配置 (阿里云通义千问)
export OPENAI_API_KEY=your-api-key
export OPENAI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
export OPENAI_MODEL_NAME=qwen3-max
export EMBEDDING_MODEL_NAME=text-embedding-v1

# 数据库配置
export DB_URL=jdbc:postgresql://localhost:5432/ai_vector
export DB_USERNAME=postgres
export DB_PASSWORD=your-password

# Redis配置
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
```

### 数据库准备

确保 PostgreSQL 已安装 pgvector 扩展：

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

### RAG文档

将知识文档放入 `src/main/resources/data/` 目录，应用启动时会自动进行向量化处理。

## 快速开始

```bash
# 克隆项目
git clone <repository-url>
cd multi-agent

# 配置环境变量
export OPENAI_API_KEY=your-api-key

# 运行
mvn spring-boot:run
```

## API接口

### 用户登录
```
POST /user/login
POST /user/send
```

### Agent对话
```
GET /gateway/dispatch?memoryId={sessionId}&message={message}
```

**示例请求:**
```
GET /gateway/dispatch?memoryId=user-123&message=帮我订阅Java课程
```

**示例响应:**
```json
已成功为您订阅Java课程！课程ID: 1
```

## 目录结构

```
src/main/java/com/multiagent/
├── agent/                        # Agent核心模块
│   ├── Agent.java                # Agent接口
│   ├── SupervisorAgent.java      # 调度Agent
│   ├── KnowledgeAgent.java       # 知识Agent
│   ├── PublishAgent.java         # 订阅Agent
│   ├── dto/                      # 数据传输对象
│   │   └── AgentRouteDecision.java
│   ├── registry/                 # Agent注册中心
│   │   └── AgentRegistry.java
│   └── dispatcher/               # Agent调度器
│       └── AgentDispatcher.java
├── common/                       # 公共模块
│   ├── config/                   # 全局配置
│   ├── entity/                   # 公共实体
│   ├── interceptor/              # 拦截器
│   └── business/                 # 用户服务
├── supervisoragent/              # 调度Agent模块
│   ├── config/                   # 会话配置
│   └── controller/               # API入口
├── knowledgeagent/               # 知识Agent模块
│   ├── config/                   # RAG配置
│   ├── rag/                      # 文档初始化
│   └── business/                 # 业务服务
└── publishagent/                 # 订阅Agent模块
    ├── config/                   # Agent配置
    ├── tool/                     # LangChain4j Tool
    └── business/                 # 业务服务
```