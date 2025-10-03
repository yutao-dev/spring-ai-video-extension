package com.springai.springaivideoextension.enhanced.option.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springai.springaivideoextension.common.util.BeanUtils;
import com.springai.springaivideoextension.common.util.JsonUtils;
import com.springai.springaivideoextension.enhanced.option.VideoOptions;
import com.springai.springaivideoextension.enhanced.param.TypedObject;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 火山视频生成选项实现类
 *
 * 该类用于封装火山平台视频生成所需的各种配置参数，通过Builder模式构建，
 * 实现了VideoOptions接口，提供视频生成的完整配置选项。
 * 支持文本到视频和图像到视频的生成方式。
 *
 * @author 王玉涛
 * @version 1.0
 * @since 2025/9/29
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HuoShanVideoOptions extends AbstractVideoOptions {

    /**
     * 视频生成的主要提示词
     * 描述期望生成的视频内容，支持自然语言描述
     */
    @JsonIgnore
    private String prompt;

    /**
     * 使用的AI模型名称
     * 指定用于视频生成的具体模型，如"video-generator-v1"
     */
    @JsonProperty("model")
    private String model;

    /**
     * 视频内容列表
     * 包含视频生成所需的多模态内容信息，如文本描述和参考图像
     */
    @JsonProperty("content")
    private List<Content> content;


    /**
     * 参考图像路径或URL
     * 用于图像到视频的生成任务，支持本地路径和网络地址
     */
    @JsonIgnore
    private String image;

    /**
     * 所有扩展参数集合
     * 存储非标准字段，在序列化时作为重要补充字段使用
     */
    @JsonIgnore
    private Map<String, TypedObject<?>> allParameters;

    /**
     * 构建发送给火山API的JSON请求体
     *
     * @return 格式化的JSON请求体字符串
     */
    @Override
    public String buildJsonBody() {
        Content textContent = new Content(ContentType.TEXT, this.prompt);
        if (Objects.isNull(this.image)) {
            this.content = List.of(textContent);
        } else {
            Content imageContent = new Content(ContentType.IMAGE_URL, this.image);
            this.content = List.of(textContent, imageContent);
        }

        return JsonUtils.writeValueAsString(this);
    }

    /**
     * 基于参数映射关系深拷贝构造新的视频选项对象
     *
     * @param params 参数映射关系，键为参数名，值为参数值包装对象
     * @return 新构建的视频选项对象
     */
    @Override
    public VideoOptions fromParameters(Map<String, TypedObject<?>> params) {
        return HuoShanVideoOptions.builder()
                .prompt(BeanUtils.nullThenChooseOther(getParameter(params, "prompt").getValue(), this.prompt, String.class))
                .model(BeanUtils.nullThenChooseOther(getParameter(params, "model").getValue(), this.model, String.class))
                .image(BeanUtils.nullThenChooseOther(getParameter(params, "image").getValue(), this.image, String.class))
                .allParameters(params)
                .build();
    }

    /**
     * 获取参数
     *
     * @param key 参数键
     * @return 参数对象
     */
    private TypedObject<?> getParameter(Map<String, TypedObject<?>> map, String key) {
        TypedObject<?> typedObject = map.get(key);
        return Objects.isNull(typedObject) ? TypedObject.getInstance() : typedObject;
    }

    /**
     * 内容项数据传输对象
     * 用于封装视频生成所需的多模态内容元素
     */
    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Content {
        /**
         * 内容类型标识
         * 如"text"表示文本内容，"image_url"表示图像链接
         */
        @JsonProperty("type")
        private String type;
        
        /**
         * 文本内容
         * 当type为"text"时有效，存储具体的文本描述
         */
        @JsonProperty("text")
        private String text;
        
        /**
         * 图像URL地址
         * 当type为"image_url"时有效，存储图像的访问地址
         */
        @JsonProperty("image_url")
        private String imageUrl;

        /**
         * 构造函数，根据内容类型和参数值初始化对象
         *
         * @param type 内容类型枚举值
         * @param param 对应类型的参数值
         */
        public Content(ContentType type, String param) {
            switch (type) {
                case TEXT:
                    this.type = type.type;
                    this.text = param;
                    break;
                case IMAGE_URL:
                    this.type = type.type;
                    this.imageUrl = param;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid content type: " + type);
            }
        }
    }

    /**
     * 内容类型枚举
     * 定义支持的内容类型及其对应的字符串标识
     */
    @RequiredArgsConstructor
    private enum ContentType {
        /**
         * 文本类型内容
         */
        TEXT("text"),
        
        /**
         * 图像URL类型内容
         */
        IMAGE_URL("image_url");

        /**
         * 类型对应的字符串标识
         */
        private final String type;
    }
}