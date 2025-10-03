# Enhanced Details 文档

## 1. 文档介绍

1. 本技术文档旨在全面记录项目的完整演进历程，涵盖详细设计方案、架构决策考量以及关键实现要点等核心内容。
2. 每个重要的演进节点将独立成章，采用结构化叙述方式，确保内容清晰且易于理解。
3. 本文档基于[飞书源文档：SpringAI 深入探索](https://dcn7850oahi9.feishu.cn/docx/DDehdPBMSoGTycxmFTLcER4In0F?from=from_copylink)进行迭代更新，专注于视频模型的深度细化与实现。

## 2. 已完成功能模块

1. 基于 SpringAI 框架规范，已成功实现视频模型的核心功能体系，包括**任务创建、定时轮询、任务查询、任务存储**等完整解决方案。
2. 当前框架基于**硅基流动**厂商的 API 接口规范进行开发构建。
3. 得益于遵循 OpenAI 标准规范的设计理念，该方案天然兼容硅基流动、OpenAI 等主流厂商的 API 接口。

## 3. 后续演进规划

1. 持续优化架构设计，提升框架的厂商适配能力，实现对多元化 API 文档的兼容支持。
2. 完整记录架构演进过程，形成标准化参考模板，并推广应用于图像模型等其他 AI 模型领域。
3. 当前只实现了基于 Map 的存储方案，后续将会继续扩展 [VideoStorage.java](file:///D:/program-test2/programming/spring-ai-video-extension/src/main/java/com/springai/springaivideoextension/enhanced/storage/VideoStorage.java) 存储方案，引入 Redis 等多样化存储机制，并基于此推动代码设计的进一步优化与演进。

## 4. 架构演进——兼容多厂商API

### 4.1 前言

1. 本次的架构演进，我们将会使用 **火山方舟官方** 提供的API文档作为演示案例。
2. 我们将会提供完整的架构演进方案，这个 **演进的过程、决策过程** 将会作为其他模态模型的演进参考方案。
3. 本次方案将会通过进一步的抽象、架构改造，实现从 **单规范多厂商适配** 到 **多规范多厂商适配** 的跃进。

### 4.2 前置分析
1. 在架构演进开始之前，我们通常需要重新捋一遍当前的架构逻辑，并根据当前的架构逻辑，进行前置分析+方案初步设计
2. 首先我们通过描述当前的架构逻辑，随后给出Mermaid流程图
   - 第一层(请求层): [VideoApi.java](api/VideoApi.java), 该类负责接收[VideoOptions.java](option/VideoOptions.java)参数，并将其作为请求体进行发送
   - 第二层(参数层): [VideoOptions.java](option/VideoOptions.java), 该类负责封装参数逻辑，一方面是作为请求时候的自定义参数，另一方面则是作为发送参数
   - 第三层(模型层): [VideoModel.java](model/VideoModel.java), 该类负责封装模型逻辑，并作为模型参数的接收者, 将参数封装为 [VideoApi.java](api/VideoApi.java), 并调用[VideoApi.java](api/VideoApi.java)进行请求发送
   - 第四层(客户端层): [VideoClient.java](client/VideoClient.java), 该类是核心类，负责统筹复杂的视频模型调用逻辑，具体可以进行如下拆分
     1. 封装请求、发送请求: 通过 param().xx().getOutput()，将请求参数封装为 [VideoOptions.java](option/VideoOptions.java), 并调用 [VideoModel.java](model/VideoModel.java) 进行第一轮的请求发送
     2. 接收响应、持久化响应: 当调用[VideoModel.java](model/VideoModel.java)，且返回值返回到客户端层后，[VideoClient.java](client/VideoClient.java) 通过 [VideoStorage.java](storage/VideoStorage.java) 进行持久化，并将结果返回给调用方
   - 应用层额外(定时任务层): [VideoTimer.java](trimer/VideoTimer.java), 该类负责从[VideoStorage.java](storage/VideoStorage.java)中获取已经存储的 requestId，并调用[VideoApi.java](api/VideoApi.java)进行查询，并根据结果，选择性调用[VideoStorage.java](storage/VideoStorage.java), 更新对应视频状态
   
3. 我们通过Mermaid，画出当前的架构流程图
    ```mermaid
    graph TD
    %% 用户发起请求
        A[Application User] -->|1. 初始化请求| B[VideoClient]
    
    %% 参数构建阶段
        B -->|2. 创建参数构造器| C[ParamBuilder]
        C -->|3. 链式调用设置参数| C
        C -->|4. 构建完成参数| B
    
    %% API调用阶段
        B -->|5. 构建VideoPrompt| D[VideoPrompt]
        D -->|包含配置| E[VideoOptions]
        B -->|6. 调用视频模型| F[VideoModel]
        F -->|7. 委托API调用| G[VideoApi]
        G -->|8. HTTP请求| H[External Video Service]
        H -->|9. 返回原始结果| G
        G -->|10. 封装响应| F
        F -->|11. 返回VideoResponse| B
    
    %% 存储阶段
        B -->|12. 检查存储配置| I{存储已配置?}
        I -->|是| J[videoStorage.save]
        I -->|否| K[log.warn 无存储配置]
        J -->|13. 保存到存储| L{存储成功?}
        L -->|是| M[记录requestId等信息]
        L -->|否| N[log.error 存储失败]
    
    %% 异步状态轮询（独立流程）
        O[VideoTimer Scheduler] -->|14. 定时触发| P[获取待处理请求]
        P -->|15. 查询存储| Q[videoStorage.getPendingRequests]
        Q -->|16. 获取requestIds列表| O
        O -->|17. 逐个查询状态| R[VideoApi.queryStatus]
        R -->|18. 调用外部服务| H
        H -->|19. 返回最新状态| R
        R -->|20. 更新存储状态| S[videoStorage.updateStatus]
    
    %% 最终返回用户
        M -->|21. 返回最终结果| T[VideoResponse to User]
        K -->|21. 返回最终结果| T
        N -->|21. 返回最终结果| T
    
    %% 样式定义
        classDef client fill:#e1f5fe,stroke:#01579b,stroke-width:2px
        classDef api fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
        classDef storage fill:#fff3e0,stroke:#e65100,stroke-width:2px
        classDef timer fill:#fce4ec,stroke:#c2185b,stroke-width:2px
        classDef external fill:#ffebee,stroke:#b71c1c,stroke-width:2px
        classDef builder fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
        classDef user fill:#e0f2f1,stroke:#004d40,stroke-width:2px
    
        class B,F client
        class G,R api
        class J,Q,S storage
        class O,P timer
        class H external
        class C builder
        class A,T user
    ```
4. 而是否可以实现多厂商、多规范的关键，就在于 **请求发送时候的请求体格式是否兼容** 。
5. 不管是Options作为直传参数、还是Api作为请求发送类，关键在于 **请求的uri 和 请求的参数格式**。
6. 这意味着，我们需要从 [VideoOptions.java](option/VideoOptions.java)接口入手，我们之前预留了该接口，并通过[VideoOptionsImpl.java](option/impl/VideoOptionsImpl.java)验证过，以接口+多态接收作为参数传递这一方法是可行的
7. 我们可以运用这一层抽象，使用策略模式，将不同厂商的参数进行适配，并实现多厂商多规范的逻辑
8. 在上述几点中，我们已经解决了参数传递层次的问题，而我们后续需要解决的，是多厂商、多规范下的兼容问题
9. 我们已经知道，调用大模型的API，需要在请求头中放入api-key，同时需要提供base-url作为请求基地址，也需要uri后缀适配
10. 我们针对VideoApi[VideoApi.java](api/VideoApi.java)目前的请求逻辑进行分析
    ```java
    public class VideoApi {
        // 这里的RestClient，会作为核心请求客户端，这里会进行请求发送，并返回结果
        private final RestClient restClient;
        // 这里的videoPath，会作为视频上传的uri，这里会进行上传请求发送，并返回结果
        private final String videoPath;
        // 这里的videoStatusPath，会作为视频查询的uri，这里会进行查询请求发送，并返回结果
        private final String videoStatusPath;
        
        /**
         * 构造函数，用于创建VideoApi实例
         * 
         * @param baseUrl 基础URL地址，作为所有API请求的根路径
         * @param apiKey API密钥，用于身份验证，如果提供了有效的ApiKey则会添加到请求头中
         * @param headers 额外的请求头信息，允许自定义请求头参数
         * @param videoPath 视频上传接口的路径后缀，将与baseUrl组合成完整的上传接口URL
         * @param videoStatusPath 视频状态查询接口的路径后缀，将与baseUrl组合成完整的查询接口URL
         * @param restClientBuilder RestClient构建器，用于构建和配置HTTP客户端
         * @param responseErrorHandler 响应错误处理器，用于处理HTTP响应中的错误情况
         */
        public VideoApi(String baseUrl, ApiKey apiKey, MultiValueMap<String, String> headers, String videoPath, String videoStatusPath, RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {
            // 使用RestClient.Builder构建RestClient实例
            this.restClient = restClientBuilder
                    // 设置基础URL
                    .baseUrl(baseUrl)
                    // 配置默认请求头
                    .defaultHeaders((h) -> {
                        // 如果提供了有效的ApiKey（不是NoopApiKey），则设置Bearer认证头
                        if (!(apiKey instanceof NoopApiKey)) {
                            h.setBearerAuth(apiKey.getValue());
                        }
                        // 设置Content-Type为JSON格式
                        h.setContentType(MediaType.APPLICATION_JSON);
                        // 添加所有自定义请求头
                        h.addAll(headers);
                    })
                    // 设置默认的错误响应处理器
                    .defaultStatusHandler(responseErrorHandler)
                    // 构建RestClient实例
                    .build();
            // 保存视频上传路径
            this.videoPath = videoPath;
            // 保存视频状态查询路径
            this.videoStatusPath = videoStatusPath;
        }
        // ...
    }
    ```

11. 我们发现：apiKey、videoPath、videoStatusPath、restClient 的关系如下：
    - apiKey 与 restClient 高度绑定，正常情况下，是不会直接更换 apiKey 的
    - videoPath、videoStatusPath 也与 restClient 有绑定，但是它们是可以随时更换的，可以通过 Options 参数自定义直传等方式解决
    - 而三者关系如何统筹，又是一道难题，单厂商，是单 uri，但是可以是多 apiKey

12. 而如果为了多厂商、多规范适配，我们是否需要沿用 [飞书源文档](https://dcn7850oahi9.feishu.cn/docx/DDehdPBMSoGTycxmFTLcER4In0F?from=from_copylink) 的自定义集群逻辑，即：
    - 通过配置文件配置多 VideoProperties
    - 再通过多 VideoProperties 实例，配置多 VideoApi，通过 modelId 进行区分，这是**"面向模型"**的集群方案，即为单个模型，配置多 apiKey、videoPath、videoStatusPath、restClient
    - 通过这样配置，我们可以实现的是：apiKey、videoPath、videoStatusPath、restClient 一体化且多配置隔离
    - 但是问题在于：大模型调用，在厂商提供方，会有很严格的并发限制，我们目前的 restClient 完全是同一类型线程池多实例，显然，这里的性能是严重溢出的

13. 此时我们需要考虑，是否要更换一下这样的侧重方式，也就是把 "面向模型" 的集群方案，更换为其他的方案：
    - 因为 **"面向模型"** 的集群方案，本质上是单种模型，配置多厂商、多账户
    - 这种情况下，会带来最大程度的模型控制精细度，不过多 VideoApi 的创建确实会带来一定程度的性能冗余
    - 但是这种情况下也是最方便维护的，关键配置全部显性化，而不是隐藏到更深的层次
    - 且当前的方案，是比较贴合 SpringAI 原生的架构设计的，如果为了灵活度，而大幅度改动底层字段，势必会造成未来的兼容性问题

14. 因此我们依旧沿用原来的自定义集群方案，即 "面向模型" 的集群方案来管理多 apiKey、videoPath、videoStatusPath、restClient。

### 4.3 总结
因此我们发现，架构设计的更新迭代需要从多个维度进行综合权衡，我们总结如下：
- 业务适配度：这是最重要的考量因素，需要评估新旧方案在业务场景中的适配程度是否在可接受范围内
- 短期成本控制：需要对比旧方案当前的维护成本与新方案短期改造成本的大小关系，同时评估新方案带来的收益是否显著高于旧方案
- 长期成本控制：评估新旧方案在长期维护方面的成本差异，以及新方案是否能带来比旧方案更显著的长期收益
- 兼容性：新旧方案之间的兼容性是否满足要求。以本例而言，旧方案在SpringAI兼容性方面表现更优，能够更快速地跟进SpringAI的更新
- 灵活性：对比新旧方案的灵活性差异是否在可接受范围内，主要体现在修改关键配置参数所需的成本
- 需要注意的是：面向接口编程的抽象设计虽然能带来更高的灵活性，但同时也增加了维护成本。过度抽象往往会导致开发和维护成本的双重提升，因此我们需要把握好抽象的度，做到适度抽象

### 4.4 代码演进

#### 4.4.1 架构分析

1. 在4.2总我们已经讨论过，后续的架构优化，主要是针对[VideoOptions.java](option/VideoOptions.java)进行多厂商适配
2. 而如何实现根据模型使用，而自动选择合适的策略，我们可以沿用源文档中的自定义架构集群的**modelId**策略
3. 通过modelId精准筛选匹配，而如果不使用modelId，我们也会尝试匹配最接近的模型

### 4.4.2 代码演进

1. 我们目前使用的，是基于硅基流动厂商提供的API文档参数
2. 我们将[VideoOptionsImpl.java](option/impl/VideoOptionsImpl.java)类拷贝一份，定义为SiliconCloudVideoOptions
3. 同时向类中添加字段，需要添加的字段如下
   - modelId: 模型唯一标识
   - modelName: 模型名称
   - modelDescription: 模型描述
4. 这些字段不参与请求发送，因此需要使用@JsonIgnore，这些字段是作为策略模式中重要的模型参数匹配信息而存在的
    ```java
    /**
     * 模型唯一标识符
     * 用于内部识别和区分不同的视频生成模型
     */
    @JsonIgnore
    private String modelId;
    
    /**
     * 模型显示名称
     * 用于前端展示和用户界面显示的模型名称
     */
    @JsonIgnore
    private String modelName;
    
    /**
     * 模型详细描述信息
     * 包含模型的功能特点、适用场景等详细说明
     */
    @JsonIgnore
    private String modelDescription;
    ```
5. 同时为了保证每个参数策略都可以被识别，需要在[VideoOptions.java](option/VideoOptions.java)额外添加这三个参数的getter方法
    ```java
    /**
     * 获取视频生成模型的唯一标识符
     * 用于区分不同的视频生成模型实例
     *
     * @return 模型ID字符串
     */
    String getModelId();
    
    /**
     * 获取视频生成模型的显示名称
     * 用于在用户界面中展示模型名称
     *
     * @return 模型名称字符串
     */
    String getModelName();
    
    /**
     * 获取视频生成模型的详细描述信息
     * 包含模型的功能特点、适用场景等说明
     *
     * @return 模型描述字符串
     */
    String getModelDescription();
    ```
6. 在其他的实现类中也实现类似的结构即可，这里我们再以[火山方舟的视频模型文档](https://www.volcengine.com/docs/82379/1520757)为例
    ```shell
    # 创建 图生视频 任务
    curl -X POST https://ark.cn-beijing.volces.com/api/v3/contents/generations/tasks \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer f16d8c81-abda-4cfe-af76-1dad86ccb914" \
      -d '{
        "model": "doubao-seedance-1-0-pro-250528",
        "content": [
            {
                "type": "text",
                "text": "无人机以极快速度穿越复杂障碍或自然奇观，带来沉浸式飞行体验  --resolution 1080p  --duration 5 --camerafixed false --watermark true"
            },
            {
                "type": "image_url",
                "image_url": {
                    "url": "https://ark-project.tos-cn-beijing.volces.com/doc_image/seepro_i2v.png"
                }
            }
        ]
    }'
    ```
    ##### 请求参数总览
    
    | 参数        | 类型       | 是否必选  | 描述                                  |
    |:----------|:---------|:------|:------------------------------------|
    | `model`   | String   | **是** | 需要调用的模型ID，例如 `doubao-seedance-pro`。 |
    | `content` | Object[] | **是** | 输入给模型的内容数组，可包含文本和图片对象。              |
    
    ---
    
    ##### `content` 对象详情
    
    ###### 1. 文本信息对象
    
    | 参数     | 类型     | 是否必选  | 描述                                                                                                |
    |:-------|:-------|:------|:--------------------------------------------------------------------------------------------------|
    | `type` | String | **是** | 必须为 `"text"`。                                                                                     |
    | `text` | String | **是** | 包含两部分：<br>1. **文本提示词**（必填）：描述期望生成的视频内容。<br>2. **参数命令**（选填）：以 `--[parameters]` 格式追加在提示词后，用于控制视频规格。 |
    
    ###### 2. 图片信息对象
    
    | 参数          | 类型     | 是否必选     | 描述                                                                                   |
    |:------------|:-------|:---------|:-------------------------------------------------------------------------------------|
    | `type`      | String | **是**    | 必须为 `"image_url"`。                                                                   |
    | `image_url` | Object | **是**    | 包含图片信息的对象。                                                                           |
    | `role`      | String | **条件必填** | 定义图片的用途：<br>- `first_frame` (首帧)<br>- `last_frame` (尾帧)<br>- `reference_image` (参考图) |
    
     `image_url` 对象
    
    | 参数    | 类型     | 是否必选  | 描述               |
    |:------|:-------|:------|:-----------------|
    | `url` | String | **是** | 图片的URL或Base64编码。 |
    
    ---
    
    ##### 其他可选参数
    
    | 参数                  | 类型      | 默认值     | 描述             |
    |:--------------------|:--------|:--------|:---------------|
    | `callback_url`      | String  | -       | 任务状态变化的回调通知地址。 |
    | `return_last_frame` | Boolean | `false` | 是否返回生成视频的尾帧图像。 |
    
    ---
    
    ##### 模型文本命令（通过 `content.text` 传递）
    
    这些参数以 `--参数名 值` 或 `--简写 值` 的形式附加在文本提示词之后。
    
    | 命令全称              | 简写     | 描述         | 常见取值/备注                                                               |
    |:------------------|:-------|:-----------|:----------------------------------------------------------------------|
    | `resolution`      | `rs`   | 视频分辨率      | `480p`, `720p`, `1080p`                                               |
    | `ratio`           | `rt`   | 视频宽高比      | `16:9`, `4:3`, `1:1`, `3:4`, `9:16`, `21:9`, `keep_ratio`, `adaptive` |
    | `duration`        | `dur`  | 视频时长（秒）    | 通常支持 3~12 秒                                                           |
    | `framespersecond` | `fps`  | 视频帧率       | `16`, `24`                                                            |
    | `watermark`       | `wm`   | 是否包含水印     | `false` (无水印), `true` (有水印)                                           |
    | `seed`            | `seed` | 控制生成随机性的种子 | `-1` (随机)，或特定整数值                                                      |
    | `camerafixed`     | `cf`   | 是否固定摄像头    | `false`, `true`                                                       |
    
    ---
    
    ##### 响应参数
    
    | 参数   | 类型     | 描述                                   |
    |:-----|:-------|:-------------------------------------|
    | `id` | String | 视频生成任务ID。需凭此ID通过“查询视频生成任务API”获取最终结果。 |
7. 根据上述的参数，我们进行VideoOptions的设置：
   - 创建[HuoShanVideoOptions.java](option/impl/HuoShanVideoOptions.java)
   - 适配化改造：因为引入了参数请求形式差异较大的火山方舟，我们这时候需要进行权衡，是否依旧保持硬性参数统一？如果要保持硬性参数统一，那么VideoOptions中就需要更复杂的适配流程
   - 而越来越多的厂商引入，反而让VideoOptions的限制逐步减弱，因此，我们选择更加灵活的参数传递方式，即通过Map<String, Object>映射
   - 而如何保证序列化过程中，不丢失类型信息？我们可以进行进一步的封装，将Object obj, Class<T> clazz 封装为一个类，在提取的时候自动获取准确类型

8. 我们对[VideoOptions.java](option/VideoOptions.java)进行适配化改造
   - 将两个实现类中截然不同的参数，统一归类为Map<String, [TypedObject](param/TypedObject.java)>
   - [TypedObject.java](param/TypedObject.java)类提供（该类具备强通用性，因此将会作为底层技术组件使用）
   - [VideoOptions.java](option/VideoOptions.java)适配

    ```java
    /**
     * 删除，因为当前的参数并不通用，将会放置于Map中
     * 获取反向提示词
     * 用于指定不希望在生成视频中出现的内容
     *
     * @return 反向提示词字符串
     */
    String getNegativePrompt();
    
    /**
     * 删除，因为当前的参数并不通用，将会放置于Map中
     * 获取随机种子值
     * 用于控制视频生成的随机性，相同种子值可产生相似结果
     *
     * @return 种子值字符串
     */
    Long getSeed();
   
   /**
     * 添加字段，该字段会包含其他的剩余参数
     * 获取所有参数
     *
     * @return 参数映射关系
     */
    Map<String, TypedObject<?>> getOtherParameters();
    ```
   - 在其他实现类中添加该字段即可
    ```java
    /**
    * 所有参数
    */
    @JsonIgnore
    private Map<String, TypedObject<?>> allParameters;
    ```
   
#### 4.4.3 流程分析

1. 经过上述的改造后，字段灵活性得到了提升，但是我们需要重新梳理一下思路：如何开展适配化改造，可以保证更加顺利？
2. 我们决定从Client入手，自顶向下，在Client阶段考虑针对非标准字段的适配化改造
3. 首先我们看当前的Client字段构造核心流程
    ```java
    /**
     * 执行视频生成请求
     * @return 视频生成响应结果
     */
    public VideoResponse call() {
        // 构建视频选项参数
        VideoOptions options = VideoOptionsImpl.builder()
                .prompt(prompt)
                .model(model)
                .imageSize(imageSize)
                .negativePrompt(negativePrompt)
                .image(image)
                .seed(seed)
                .build();
        // 创建视频提示对象
        VideoPrompt videoPrompt = new VideoPrompt(prompt, options);
        // 调用视频生成接口
        return VideoClient.this.call(videoPrompt);
    }
    ```
4. 首先我们可以看到，这里并不能很好的兼容非标准化字段，因此我们需要进行演进
5. 在Client的参数构建阶段，只保留prompt、image、model标准字段，同步添加modelId、modelName、modelDescription业务字段
6. 同时提供通用方式，即paramSet(String paramName, Object value, Class<?> clazz)，我们决定使用 **约定大于配置**，需要使用者遵循规定使用，避免为每个厂商而添加大量代码
7. 修改如下
    ```java
    /**
     * 参数构建器类，用于构建视频生成请求参数
     */
    public class ParamBuilder {
        // 视频生成提示词
        private String prompt;
        // 使用的模型名称
        private String model;
        // 参考图像路径
        private String image;
        // 模型唯一标识
        private String modelId;
    
        private Map<String, TypedObject<?>> params;
    
        /**
         * 设置视频生成提示词
         * @param prompt 提示词
         * @return 参数构建器实例
         */
        public ParamBuilder prompt(String prompt) {
            this.prompt = prompt;
            this.params.put("prompt", TypedObject.valueOf(prompt, String.class));
            return this;
        }
    
        /**
         * 设置使用的模型名称
         * @param model 模型名称
         * @return 参数构建器实例
         */
        public ParamBuilder model(String model) {
            this.model = model;
            this.params.put("model", TypedObject.valueOf(model, String.class));
            return this;
        }
        
        public ParamBuilder modelId(String modelId) {
            this.modelId = modelId;
            this.params.put("modelId", TypedObject.valueOf(modelId, String.class));
            return this;
        }
        
        /**
         * 设置参考图像路径
         * @param image 图像路径
         * @return 参数构建器实例
         */
        public ParamBuilder image(String image) {
            this.image = image;
            this.params.put("image", TypedObject.valueOf(image, String.class));
            return this;
        }
        
        public ParamBuilder paramSet(String parmName, Object value, Class<?> type) {
            this.params.put(parmName, TypedObject.valueOf(value, type));
            return this;
        }
                
        /**
         * 执行视频生成请求
         * @return 视频生成响应结果
         */
        public VideoResponse call() {
            Assert.isTrue(StringUtils.hasText(model), "模型名称不能为空");
            Assert.isTrue(StringUtils.hasText(prompt), "视频生成提示词不能为空");
            LoggerUtils.logWarnIfTrue(StringUtils.hasText(model), "模型名称未指定, 将使用默认模型, 可能会出现错误！");
            LoggerUtils.logWarnIfTrue(StringUtils.hasText(image), "参考图像未指定, 若模型不对，可能会出现错误！");
            
            // 根据模型名称获取模型参数
            VideoOptions videoOptions = videoOptionsFactory.getVideoOptions(modelId);
            // 如果无法准确获取modelId，那么将会尝试通过模型名称获取(并发出警告)
            videoOptions = Objects.isNull(videoOptions) ? videoOptionsFactory.getVideoOptionsByModel(model) : videoOptions;
            // 保存所有的参数，构建请求参数将会以这个为准
            videoOptions.setAllParameters(this.params);
            
            // 创建视频提示对象
            VideoPrompt videoPrompt = new VideoPrompt(prompt, options);
            // 调用视频生成接口
            return VideoClient.this.call(videoPrompt);
        }
    
        /**
         * 获取视频生成结果的输出信息
         * @return 输出信息
         */
        public String getOutput() {
            return this.call().getResult().getOutput();
        }
    }
    ```
8. 在这里，我们只留下了必要参数，这些参数作为校验参数使用，而Map将会作为重要的字段，参与后续的请求流程
9. 我们继续完善其中的逻辑，首先看VideoOptions videoOptions = VideoOptionsFactory.getOptions(modelId);
   - 这里使用到了策略模式，通过modelId与VideoOptions的实现类建立映射关系，我们通过Map优化这一映射关系的存储形式
   - 我们需要将VideoOptionsFactory纳入Bean容器管理，因为这样可以直接使用Spring原生的依赖注入功能，只需要在Client中注入该工厂即可
    ```java
    /**
     * 视频选项工厂类，用于管理和获取不同模型的视频选项配置
     *
     * @author 王玉涛
     * @version 1.0
     * @since 2025/10/3
     */
    @Slf4j
    @Component
    public class VideoOptionsFactory {
    
        /**
         * 存储模型ID与视频选项映射关系的Map
         * key: 模型ID
         * value: 对应的视频选项配置
         */
        private final Map<String, VideoOptions> videoOptionsMap;
    
        /**
         * 构造函数，初始化视频选项工厂
         *
         * @param videoOptionsList 视频选项列表，包含各种模型的配置信息
         * @throws VideoModelOptionsInitException 当视频选项列表为null时抛出初始化异常
         */
        public VideoOptionsFactory(List<VideoOptions> videoOptionsList) {
            log.info("初始化视频选项工厂: {}", videoOptionsList);
            // 检查输入参数是否为空
            if (videoOptionsList == null) {
                log.warn("视频选项工厂初始化失败, 请检查配置");
                throw new VideoModelOptionsInitException("视频选项工厂初始化失败, 请检查配置");
            }
            // 初始化映射表
            videoOptionsMap = new HashMap<>();
            // 遍历视频选项列表，建立模型ID与选项的映射关系
            for (VideoOptions videoOptions : videoOptionsList) {
                String modelId = videoOptions.getModelId();
                videoOptionsMap.put(modelId, videoOptions);
            }
    
            log.info("视频选项工厂初始化完成: {}", videoOptionsMap);
        }
    
        /**
         * 根据模型ID获取对应的视频选项配置
         *
         * @param modelId 模型ID
         * @return 对应的视频选项配置
         * @throws VideoModelOptionsInitException 当找不到对应模型ID的视频选项时抛出异常
         */
        public VideoOptions getVideoOptions(String modelId) {
            VideoOptions videoOptions = videoOptionsMap.get(modelId);
            // 检查是否存在对应的视频选项配置
            if (videoOptions == null) {
                log.warn("未找到对应的视频选项: {}", modelId);
                throw new VideoModelOptionsInitException("未能根据modelId找到对应的视频选项: " + modelId);
            }
            return videoOptions;
        }
    
        /**
         * 根据模型名称获取对应的视频选项配置
         *
         * @param model 模型名称
         * @return 对应的视频选项配置
         * @throws VideoModelOptionsInitException 当找不到对应模型名称的视频选项时抛出异常
         */
        public VideoOptions getVideoOptionsByModel(String model) {
            return videoOptionsMap.entrySet()
                    .stream()
                    .filter(videoOptionsMap -> videoOptionsMap.getValue().getModel().equals(model))
                    .findAny()
                    .orElseThrow(() -> new VideoModelOptionsNotFoundException("未能根据model找到对应的视频选项: " + model))
                    .getValue();
        }
    }
    ```