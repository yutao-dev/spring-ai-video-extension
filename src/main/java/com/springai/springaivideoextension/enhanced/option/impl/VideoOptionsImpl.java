package com.springai.springaivideoextension.enhanced.option.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springai.springaivideoextension.common.util.BeanUtils;
import com.springai.springaivideoextension.common.util.JsonUtils;
import com.springai.springaivideoextension.enhanced.option.VideoOptions;
import com.springai.springaivideoextension.enhanced.param.TypedObject;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 视频生成选项实现类
 *
 * 该类用于封装视频生成所需的各种配置参数，通过Builder模式构建，
 * 实现了VideoOptions接口，提供视频生成的完整配置选项。
 *
 * @author 王玉涛
 * @version 1.0
 * @since 2025/9/29
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoOptionsImpl implements VideoOptions {

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

    /**
     * 视频生成的主要提示词
     * 描述期望生成的视频内容
     */
    @JsonProperty("prompt")
    private String prompt;

    /**
     * 使用的AI模型名称
     * 指定用于视频生成的具体模型
     */
    @JsonProperty("model")
    private String model;

    /**
     * 生成视频的尺寸规格
     * 格式如: "1024x1024", "1280x720" 等
     */
    @JsonProperty("image_size")
    private String imageSize;

    /**
     * 负面提示词
     * 指定不希望在生成视频中出现的内容
     */
    @JsonProperty("negative_prompt")
    private String negativePrompt;

    /**
     * 参考图像路径或URL
     * 用于图像到视频的生成任务
     */
    @JsonProperty("image")
    private String image;

    /**
     * 随机种子
     * 用于控制生成过程的随机性，相同种子可产生一致结果
     */
    @JsonProperty("seed")
    private Long seed;

    @JsonIgnore
    private Map<String, TypedObject<?>> allParameters;


    /**
     * 构建JSON请求体
     *
     * @return JSON请求体字符串
     */
    @Override
    public String buildJsonBody() {
        return JsonUtils.writeValueAsString(this);
    }

    /**
     * 深拷贝所有参数
     *
     * @param params 参数映射关系
     */
    @Override
    public VideoOptions fromParameters(Map<String, TypedObject<?>> params) {
        return VideoOptionsImpl.builder()
                .modelId(BeanUtils.nullThenChooseOther(params.get("modelId"), this.modelId, String.class))
                .modelName(BeanUtils.nullThenChooseOther(params.get("modelName"), this.modelName, String.class))
                .modelDescription(BeanUtils.nullThenChooseOther(params.get("modelDescription"), this.modelDescription, String.class))
                .prompt(BeanUtils.nullThenChooseOther(params.get("prompt"), this.prompt, String.class))
                .model(BeanUtils.nullThenChooseOther(params.get("model"), this.model, String.class))
                .imageSize(BeanUtils.nullThenChooseOther(params.get("imageSize"), this.imageSize, String.class))
                .negativePrompt(BeanUtils.nullThenChooseOther(params.get("negativePrompt"), this.negativePrompt, String.class))
                .image(BeanUtils.nullThenChooseOther(params.get("image"), this.image, String.class))
                .seed(BeanUtils.nullThenChooseOther(params.get("seed"), this.seed, Long.class))
                .allParameters(params)
                .build();
    }

}