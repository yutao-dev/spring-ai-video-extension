package com.springai.springaivideoextension.enhanced.client;

import com.springai.springaivideoextension.common.util.LoggerUtils;
import com.springai.springaivideoextension.enhanced.model.VideoModel;
import com.springai.springaivideoextension.enhanced.model.request.VideoPrompt;
import com.springai.springaivideoextension.enhanced.model.response.VideoResponse;
import com.springai.springaivideoextension.enhanced.option.VideoOptions;
import com.springai.springaivideoextension.enhanced.option.factory.VideoOptionsFactory;
import com.springai.springaivideoextension.enhanced.option.impl.VideoOptionsImpl;
import com.springai.springaivideoextension.enhanced.param.TypedObject;
import com.springai.springaivideoextension.enhanced.storage.VideoStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/9/30
 */
@Slf4j
public class VideoClient {

    // 视频模型接口，用于调用视频生成服务
    private final VideoModel videoModel;
    // 视频存储接口，用于持久化视频生成结果
    private final VideoStorage videoStorage;

    @Resource
    private VideoOptionsFactory videoOptionsFactory;

    /**
     * 构造函数，仅指定视频模型
     * @param videoModel 视频模型实例
     */
    public VideoClient(VideoModel videoModel) {
        this.videoModel = videoModel;
        this.videoStorage = null;
    }

    /**
     * 构造函数，指定视频模型和存储接口
     * @param videoModel 视频模型实例
     * @param videoStorage 视频存储实例
     */
    public VideoClient(VideoModel videoModel, VideoStorage videoStorage) {
        this.videoModel = videoModel;
        this.videoStorage = videoStorage;
    }

    /**
     * 调用视频模型生成视频，并根据配置决定是否持久化结果
     * @param videoPrompt 视频生成请求参数
     * @return 视频生成响应结果
     */
    private VideoResponse call(VideoPrompt videoPrompt) {
        // 调用视频模型生成视频
        VideoResponse videoResponse = this.videoModel.call(videoPrompt);

        // 获取生成结果的输出信息
        String output = videoResponse.getResult().getOutput();
        log.info("视频生成结果: {}", output);

        // 如果未配置存储接口，则直接返回结果
        if (Objects.isNull(this.videoStorage)) {
            log.warn("未指定持久化容器，将返回原始结果");
            return videoResponse;
        }

        // 持久化视频生成结果
        log.info("开始持久化请求结果");
        boolean save = this.videoStorage.save(output);
        if (!save) {
            log.warn("持久化失败, 请求id: {}", output);
        }

        return videoResponse;
    }

    /**
     * 创建参数构建器实例
     * @return 参数构建器
     */
    public ParamBuilder param() {
        return new ParamBuilder();
    }

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
            // 深拷贝所有参数
            videoOptions = videoOptions.fromParameters(this.params);

            // 创建视频提示对象
            VideoPrompt videoPrompt = new VideoPrompt(prompt, videoOptions);
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
}
