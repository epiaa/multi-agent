# Multi-Agent

基于 **Spring Boot + LangChain4j + LangGraph4j** 的多智能体系统，采用 **ReAct 编排模式** 实现智能任务调度与专业 Agent 协作。

## 核心特性

-  **ReAct 编排模式**：Think-Act-Observe 循环推理，智能决策任务路由
-  **多 Agent 协作**：统一编排器协调多个专业 Agent 工作
-  **RAG 知识问答**：基于 PgVector 的向量检索，提供精准知识服务
-  **Tool 自动调用**：LLM 自动决策并执行工具函数，完成复杂业务操作
-  **会话记忆管理**：基于 Redis 的多轮对话上下文存储

## 项目架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         用户请求                                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│              Orchestrator (ReAct 编排器)                          │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │              LangGraph4j 状态图引擎                          │  │
│  │                                                              │  │
│  │   ┌─────────┐      ┌─────────┐      ┌──────────┐          │  │
│  │   │  Think  │ ──── │   Act   │ ──── │ Observe  │          │  │
│  │   │  Node   │      │  Node   │      │   Node   │          │  │
│  │   └─────────┘      └─────────┘      └──────────┘          │  │
│  │       │                │                 │                  │  │
│  │    思考决策         执行Agent          观察结果            │  │
│  │    路由选择         工具调用          状态更新            │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────┬───────────────────────────────────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
            ▼              ▼              ▼
    ┌───────────────┐ ┌──────────────┐ ┌──────────────┐
    │   RAGAgent    │ │ PublishAgent │ │AssistantAgent│
    │  (知识问答)    │ │  (课程订阅)   │ │  (通用助手)   │
    │               │ │              │ │              │
    │ • 向量检索    │ │ • Tool调用   │ │ • 简单对话    │
    │ • PgVector    │ │ • 数据库操作  │ │ • 问候应答    │
    │ • 知识增强    │ │ • 订阅管理    │ │ • 功能介绍    │
    └───────────────┘ └──────────────┘ └──────────────┘
```

## ReAct 编排流程

```
用户: "帮我订阅Java课程并介绍一下什么是微服务"
     ↓
【Think Node】分析任务
     Thought: 用户有两个需求：1) 订阅课程 → publish-agent
              2) 询问微服务知识 → rag-agent
              先处理订阅，再回答知识问题
     Action: {"agent": "publish-agent", "task": "订阅Java课程"}
     ↓
【Act Node】执行 Agent
     调用 PublishAgent
     → Tool: subscribeCourse("Java")
     → 数据库操作
     ↓
【Observe Node】观察结果
     Observation: "已成功订阅Java课程，课程ID: 1"
     记录到历史
     ↓
【Think Node】继续推理
     Thought: 订阅完成，现在需要回答微服务问题
     Action: {"agent": "rag-agent", "task": "介绍微服务架构"}
     ↓
【Act Node】执行 Agent
     调用 RAGAgent
     → 向量检索相关文档
     → 知识增强生成
     ↓
【Observe Node】观察结果
     Observation: "微服务架构是一种..."
     记录到历史
     ↓
【Think Node】得出结论
     Thought: 所有任务已完成
     Final Answer: 已为您订阅Java课程！微服务架构是...
```

## 核心组件

### 1. Agent 接口与实现

```java
public interface Agent {
    String name();                              // Agent 名称
    boolean support(AgentRequest request);      // 支持判断
    AgentResponse execute(AgentRequest request);// 执行逻辑
}
```

**三大专业 Agent：**

| Agent | 功能 | 技术实现 | 适用场景 |
|-------|------|----------|----------|
| **RAGAgent** | 知识问答 | PgVector + 向量检索 | 技术概念、课程内容查询 |
| **PublishAgent** | 课程订阅 | Tool 自动调用 + 数据库操作 | 订阅/取消订阅课程 |
| **AssistantAgent** | 通用对话 | LLM 直接响应 | 简单问候、功能介绍 |

### 2. ReAct 编排节点

- **ThinkNode**：分析当前状态，决策下一步行动（调用 Agent 或直接回答）
- **ActNode**：执行选定的 Agent，获取执行结果
- **ObserveNode**：观察执行结果，更新历史记录

### 3. 状态管理

```java
public class ReActState {
    private String thought;              // 思考内容
    private AgentAction action;          // Agent 调用决策
    private String observation;          // 观察结果
    private List<StepRecord> history;    // 历史记录
    private boolean finished;            // 是否完成
    private String finalAnswer;          // 最终答案
    private int iterations;              // 迭代次数
}
```

## 技术栈

| 类别 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **核心框架** | Spring Boot | 3.2.7 | 应用框架 |
| | Java | 17 | 编程语言 |
| **AI 框架** | LangChain4j | 1.0.1-beta6 | LLM 应用开发框架 |
| | LangGraph4j | 1.5.10 | 状态图编排引擎 |
| **LLM 服务** | 阿里云通义千问 | qwen3-max | 大语言模型 |
| | DashScope Embedding | text-embedding-v2 | 向量化模型 |
| **数据存储** | PostgreSQL | - | 关系数据库 |
| | pgvector | - | 向量扩展 |
| | Redis | - | 会话存储 |
| **工具库** | MyBatis-Plus | 3.5.12 | ORM 框架 |
| | Hutool | 5.8.27 | Java 工具集 |

## 项目结构

```
src/main/java/com/multiagent/
├── agent/                           # Agent 核心模块
│   ├── Agent.java                   # Agent 接口定义
│   ├── AgentFactory.java            # Agent 工厂（注册中心）
│   ├── AgentRequest.java            # 请求封装
│   ├── AgentResponse.java           # 响应封装
│   ├── RAGAgent.java                # 知识问答 Agent
│   ├── PublishAgent.java            # 课程订阅 Agent
│   └── AssistantAgent.java          # 通用助手 Agent
│
├── orchestrator/                    # 编排器模块（核心）
│   ├── config/
│   │   └── AssistantAgentService.java    # LLM 服务配置
│   ├── controller/
│   │   └── OrchestratorController.java   # API 入口
│   ├── dispatcher/
│   │   └── AgentDispatcher.java          # 任务分发器
│   ├── node/                             # ReAct 节点
│   │   ├── ThinkNode.java               # 思考节点
│   │   ├── ActNode.java                 # 执行节点
│   │   └── ObserveNode.java             # 观察节点
│   ├── prompt/
│   │   └── ReActPrompt.java             # ReAct 提示词
│   └── react/
│       ├── ReActConfig.java             # 状态图配置
│       └── ReActState.java              # 状态定义
│
├── toolagent/                       # 工具 Agent 实现
│   ├── ragagent/                    # RAG Agent 实现
│   │   ├── config/
│   │   │   ├── RAGAgentService.java      # 服务接口
│   │   │   └── RAGAgentServiceImpl.java  # RAG 实现
│   │   └── rag/
│   │       └── DocumentInitializer.java  # 文档向量化
│   └── publishagent/                # Publish Agent 实现
│       ├── config/
│       │   ├── PublishAgentService.java     # 服务接口
│       │   └── PublishAgentServiceImpl.java # Tool 调用实现
│       ├── tool/
│       │   └── PublishAgentTool.java       # 订阅工具
│       └── business/
│           └── CourseService.java          # 业务服务
│
└── common/                          # 公共模块
    ├── config/                      # 全局配置
    ├── entity/                      # 实体类
    ├── interceptor/                 # 拦截器
    └── business/                    # 用户服务
```

## 环境配置

### 1. 必需的环境变量

```bash
# LLM API 配置（阿里云通义千问）
export OPENAI_API_KEY=your-api-key
export OPENAI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
export OPENAI_MODEL_NAME=qwen3-max
export EMBEDDING_MODEL_NAME=text-embedding-v2

# PostgreSQL 数据库
export DB_URL=jdbc:postgresql://localhost:5432/ai_vector
export DB_USERNAME=postgres
export DB_PASSWORD=your-password

# Redis 缓存
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
```

### 2. 数据库准备

确保 PostgreSQL 已安装并启用 pgvector 扩展：

```sql
-- 创建数据库
CREATE DATABASE ai_vector;

-- 连接数据库后启用扩展
CREATE EXTENSION IF NOT EXISTS vector;
```

### 3. RAG 知识文档

将 Markdown 文档放入 `src/main/resources/data/` 目录，应用启动时会自动进行向量化处理并存储到 PgVector。

示例：
```
src/main/resources/data/
└── Java从入门到精通.md
```

## 快速开始

```bash
# 1. 克隆项目
git clone <repository-url>
cd multi-agent

# 2. 配置环境变量
export OPENAI_API_KEY=your-api-key

# 3. 启动 PostgreSQL 和 Redis
# 确保 pgvector 扩展已安装

# 4. 运行项目
mvn spring-boot:run

# 应用将在 http://localhost:8081 启动
```

## API 接口

### 统一对话入口

```http
GET /orchestrator/dispatch?memoryId={sessionId}&message={message}
```

**参数说明：**
- `memoryId`: 会话 ID（用于多轮对话）
- `message`: 用户消息

**示例请求：**

```bash
# 订阅课程
curl "http://localhost:8081/orchestrator/dispatch?memoryId=user-123&message=帮我订阅Java课程"

# 知识问答
curl "http://localhost:8081/orchestrator/dispatch?memoryId=user-123&message=什么是微服务架构？"

# 简单对话
curl "http://localhost:8081/orchestrator/dispatch?memoryId=user-123&message=你好"
```

**示例响应：**

```text
已成功为您订阅Java课程！课程ID: 1

微服务架构是一种将单一应用程序划分成一组小服务的架构风格，每个服务运行在独立的进程中...
```

## 核心配置说明

### application.yaml

```yaml
langchain4j:
  open-ai:
    chat-model:
      api-key: ${OPENAI_API_KEY}
      base-url: ${OPENAI_BASE_URL}
      model-name: ${OPENAI_MODEL_NAME}
      temperature: 0.3          # 降低随机性，提高决策准确性
      max-tokens: 2000
      timeout: 60s

    embedding-model:
      model-name: ${EMBEDDING_MODEL_NAME}
      # 用于文档向量化

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
```

## 开发指南

### 添加新的 Agent

1. 实现 `Agent` 接口：

```java
@Component
public class MyCustomAgent implements Agent {
    @Override
    public String name() {
        return "custom-agent";
    }

    @Override
    public boolean support(AgentRequest request) {
        return request.getMessage().contains("关键词");
    }

    @Override
    public AgentResponse execute(AgentRequest request) {
        // 实现业务逻辑
        return AgentResponse.builder()
            .output("处理结果")
            .handled(true)
            .build();
    }
}
```

2. 在 `AgentFactory` 中注册：

```java
public AgentFactory(
    RAGAgent ragAgent,
    PublishAgent publishAgent,
    AssistantAgent assistantAgent,
    MyCustomAgent customAgent  // 新增
) {
    agents.put("custom-agent", customAgent);
}
```

3. 更新 `ReActPrompt` 中的 Agent 列表说明

### 自定义 ReAct 提示词

编辑 `ReActPrompt.REACT_PROMPT`，调整：
- Agent 描述和使用场景
- 输出格式要求
- 示例对话

## 技术亮点

1. **状态图编排**：使用 LangGraph4j 构建可维护的工作流
2. **ReAct 推理**：LLM 自主决策行动，支持多轮推理
3. **向量检索**：PgVector 高效语义搜索
4. **Tool 自动调用**：LLM 自动选择并执行工具
5. **会话管理**：Redis 持久化对话上下文
