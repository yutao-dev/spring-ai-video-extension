# Spring AI Video Extension - Quickstart

本模块是基于 Spring AI 框架构建的视频生成扩展快速入门模块。它严格遵循 Spring AI 的核心设计哲学与架构规范，为开发者提供了一套完整的视频处理解决方案，涵盖视频生成、数据存储以及任务状态管理等核心功能。

## 📁 项目结构

```
quickstart/
├── controller/                      # Web 控制器
│   └── ImageController.java         # 图像处理控制器
├── service/                         # 业务服务层
│   └── ImageService.java            # 图像处理服务
```

## 🌐 Web API 接口

本项目提供了 RESTful API 接口，方便通过 HTTP 请求调用视频生成功能。

### 1. 生成视频

```
POST /api/videos
```

**请求参数：**

| 参数名            | 类型     | 必填 | 说明                                   |
|----------------|--------|----|--------------------------------------|
| prompt         | String | 是  | 视频生成提示词                              |
| model          | String | 否  | 使用的模型名称，默认为 `Wan-AI/Wan2.2-T2V-A14B` |
| videoSize      | String | 否  | 生成视频的尺寸                              |
| negativePrompt | String | 否  | 负面提示词，排除不希望出现的内容                     |
| image          | String | 否  | 参考图像路径                               |
| seed           | Long   | 否  | 随机种子，用于控制生成的一致性                      |

**响应示例：**

```json
{
  "code": 200,
  "data": {
    "requestId": "req-1234567890",
  },
  "message": "视频生成成功"
}
```

### 2. 查询视频生成状态

```
GET /api/video/status/{requestId}
```

**路径参数：**

| 参数名       | 类型     | 必填 | 说明       |
|-----------|--------|----|----------|
| requestId | String | 是  | 视频生成请求ID |

**响应示例：**

```json
{
  "code": 200,
  "data": {
    "requestId": "req-1234567890",
  },
  "message": "视频生成成功"
}
```

### 3. 获取视频结果

```
GET /api/video/result/{requestId}
```

**路径参数：**

| 参数名       | 类型     | 必填 | 说明       |
|-----------|--------|----|----------|
| requestId | String | 是  | 视频生成请求ID |

**响应示例：**

```json
{
  "success": true,
  "requestId": "req-1234567890",
  "videoUrl": "https://example.com/video.mp4"
}
```

## 🧪 使用示例

```java
// 1. 构建视频选项
VideoOptions options = VideoOptionsImpl.builder()
        .prompt("一只柯基在沙滩奔跑")
        .model("Wan-AI/Wan2.2-T2V-A14B")
        .negativePrompt("模糊,低质量")
        .build();

// 2. 构建视频 API 客户端
VideoApi videoApi = VideoApi.builder()
        .apiKey("your-api-key")
        .baseUrl("https://api.video-service.com")
        .videoPath("v1/video/submit")
        .build();

// 3. 构建视频模型和客户端
VideoModel videoModel = new VideoModelImpl(videoApi);
VideoStorage videoStorage = new InMemoryVideoStorage();
VideoClient videoClient = new VideoClient(videoModel, videoStorage);

// 4. 调用视频生成
String requestId = videoClient.param()
        .prompt("一只柯基在沙滩奔跑")
        .model("Wan-AI/Wan2.2-T2V-A14B")
        .negativePrompt("模糊,低质量")
        .getOutput();

System.out.println("视频生成请求ID: " + requestId);
```

## 📦 依赖

- Spring Boot 3.5.6
- Spring AI 1.0.2
- Spring Web
- Spring AI OpenAI Starter

## 📄 许可证

本项目基于 Spring AI 框架开发，遵循相应的许可证协议。