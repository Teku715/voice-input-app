# 🎤 语音输入法 AI 优化助手

基于 Web Speech API + AI 的智能输入法，支持语音录入和 AI 文本优化。

## 功能特性

- 🎙️ **语音识别**：使用浏览器原生 Web Speech API，支持中文语音输入
- ✨ **AI 优化**：接入硅基流动 Qwen2.5-7B 模型，对输入文本进行智能优化
- 📋 **一键复制**：优化后的文本可直接复制使用
- 🎨 **现代界面**：渐变背景，操作简单直观

## 技术栈

### 前端
- HTML5 + CSS3 + JavaScript（原生）
- Web Speech API（语音识别）
- Fetch API（后端通信）

### 后端
- Java 11
- Spring Boot 2.7.14
- 硅基流动 Qwen2.5-7B API

### 第三方依赖
- Spring Boot Starter Web（Apache 2.0）
- Spring Boot Starter Tomcat（Apache 2.0）

## 运行项目

### 前端
直接用浏览器打开 `frontend/index.html` 即可使用。

### 后端
```bash
cd backend
mvn compile
mvn spring-boot:run
```
后端运行在 `http://localhost:8080`

### AI 优化接口
POST `http://localhost:8080/api/improve`
```json
{"text": "你的输入文字"}
```

## 项目结构

```
voice-input-app/
├── frontend/
│   └── index.html      # 前端页面
├── backend/
│   ├── pom.xml         # Maven 配置
│   └── src/
│       └── main/java/com/voiceinput/
│           ├── VoiceInputApplication.java
│           ├── controller/
│           │   └── VoiceController.java
│           └── service/
│               └── LLMService.java
└── README.md
```

## 接口说明

### POST /api/improve
AI 文本优化接口

**请求：**
```json
{"text": "原始文字"}
```

**响应：**
```json
{"result": "优化后的文字"}
```

### GET /api/health
健康检查接口

**响应：**
```json
{"status": "ok"}
```

## 作者

特列努尔 @ Teku715

## License

MIT