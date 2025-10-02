# Spring AI Video Extension - Quickstart

本模块是基于 Spring AI 框架构建的视频生成扩展快速入门模块。它严格遵循 Spring AI 的核心设计哲学与架构规范，为开发者提供了一套完整的视频处理解决方案，涵盖视频生成、数据存储以及任务状态管理等核心功能。

## 📁 项目结构

```
quickstart/
├── controller/                      # Web 控制器
│   └── VideoController.java         # 视频处理控制器
├── service/                         # 业务服务层
│   └── VideoService.java            # 视频处理服务
├── bean/                            # 数据传输对象
│   ├── request/              
│   │   └── GenerateVideoRequest.java # 视频生成请求
│   └── response/             
│       ├── GenerateVideoResponse.java # 视频生成响应
│       ├── VideoStatusResponse.java # 视频状态响应
│       └── Result.java              # 通用响应封装
```

## ⚠️ 常见问题与踩坑提示

### API使用注意事项
1. **参数校验**：
   - `prompt` 参数为必填项，不能为空
   - `image` 参数支持本地文件路径和网络图片URL
   - 使用图生视频时，确保图片可以正常访问

2. **模型自动切换**：
   - 当提供 `image` 参数时，系统会自动切换到图生视频模型 `Wan-AI/Wan2.2-I2V-A14B`
   - 当未提供 `image` 参数时，使用文生视频模型 `Wan-AI/Wan2.2-T2V-A14B`

3. **异步处理机制**：
   - 视频生成是异步过程，接口立即返回 requestId
   - 需要通过 `/api/videos/status/{requestId}` 接口轮询获取结果
   - 视频生成时间通常在几分钟左右，请耐心等待

### 状态查询注意事项
1. **状态码说明**：
   - `InQueue`：任务排队中
   - `InProgress`：视频生成中
   - `Succeed`：生成成功，返回视频URL
   - `Failed`：生成失败

2. **轮询策略**：
   - 建议间隔10-30秒轮询一次状态
   - 避免过于频繁的轮询请求
   - 生成成功后，视频URL有一定有效期，请及时保存

### 快速配置Client
```java
/**
 * 增强版视频服务配置类
 * 
 * 该配置类负责初始化视频相关的Bean组件，包括视频API客户端和视频存储服务。
 * 通过读取应用配置属性来构建视频API客户端，并提供内存存储的默认实现。
 * 
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/2
 */
@Slf4j
@Configuration
public class EnhancedVideoConfig {
    
    /**
     * OpenAI API密钥，从配置文件中读取
     * 用于视频处理服务的身份验证
     */
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    
    /**
     * OpenAI API基础URL，从配置文件中读取
     * 用于指定视频处理服务的访问地址
     */
    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;
    
    /**
     * 创建视频API客户端Bean
     * 
     * 使用Builder模式构建VideoApi实例，配置了API密钥、基础URL以及
     * 视频提交和状态查询的路径端点。
     * 
     * @return 配置完成的VideoApi实例
     */
    @Bean
    public VideoApi videoApi() {
        return VideoApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .videoPath("/v1/video/submit")
                .videoStatusPath("/v1/video/status")
                .build();
    }


    /**
     * 创建默认视频选项Bean
     * 
     * 提供视频生成的默认配置，包括提示词和模型选择。
     * 默认模型设置为"Wan-AI/Wan2.2-T2V-A14B"。
     * 
     * @return 默认视频选项配置
     */
    @Bean
    public VideoOptions defaultVideoOptions() {
        return VideoOptionsImpl.builder()
                .prompt("")
                .model(VideoGenerationModel.QWEN_TEXT_TO_VIDEO.getModel())
                .build();
    }

    /**
     * 创建视频模型Bean
     * 
     * 结合视频API客户端和默认选项创建视频模型实现。
     * 作为视频处理的核心业务逻辑层。
     * 
     * @return 配置的视频模型实例
     */
    @Bean
    public VideoModel videoModel() {
        return new VideoModelImpl(videoApi(), defaultVideoOptions());
    }

    /**
     * 创建视频客户端Bean
     * 
     * 通过组合视频模型和存储服务构建视频操作的主要入口点。
     * 提供视频处理的高层接口。
     * 
     * @return 配置的视频客户端实例
     */
    @Bean
    public VideoClient videoClient(VideoStorage videoStorage) {
        return new VideoClient(videoModel(), videoStorage);
    }
}
```

## 🌐 Web API 接口

本项目提供了 RESTful API 接口，方便通过 HTTP 请求调用视频生成功能。

### 1. 生成视频

```
POST /api/videos
```

**请求参数：**

| 参数名            | 类型     | 必填 | 说明                         |
|----------------|--------|----|----------------------------|
| prompt         | String | 是  | 视频生成提示词                    |
| model          | String | 否  | 使用的模型名称，默认根据是否有image参数自动选择 |
| videoSize      | String | 否  | 生成视频的尺寸，如 "512*512"        |
| negativePrompt | String | 否  | 负面提示词，排除不希望出现的内容           |
| image          | String | 否  | 参考图像路径或URL                 |
| seed           | Long   | 否  | 随机种子，用于控制生成的一致性            |

**响应示例：**

```json
{
  "code": 200,
  "data": {
    "requestId": "req-1234567890"
  },
  "message": "success"
}
```

### 2. 查询视频生成状态

```
GET /api/videos/status/{requestId}
```

**路径参数：**

| 参数名       | 类型     | 必填 | 说明       |
|-----------|--------|----|----------|
| requestId | String | 是  | 视频生成请求ID |

**响应示例（处理中）：**

```json
{
  "code": 200,
  "data": {
    "requestId": "req-1234567890",
    "videoUrl": null
  },
  "message": "success"
}
```

**响应示例（已完成）：**

```json
{
  "code": 200,
  "data": {
    "requestId": "req-1234567890",
    "videoUrl": "https://example.com/video.mp4"
  },
  "message": "success"
}
```

## 🧪 使用示例

### curl 示例

**生成视频（文生视频）：**
```bash
curl -X POST http://localhost:8080/api/videos \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "一只可爱的猫咪在花园里玩耍",
    "videoSize": "512*512",
    "negativePrompt": "模糊,低质量"
  }'
```

**生成视频（图生视频）：**
```bash
curl -X POST http://localhost:8080/api/videos \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "让图片中的人物动起来",
    "image": "https://example.com/image.jpg",
    "videoSize": "512*512"
  }'
```

**查询视频状态：**
```bash
curl -X GET http://localhost:8080/api/videos/status/req-1234567890
```

### Java 示例

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