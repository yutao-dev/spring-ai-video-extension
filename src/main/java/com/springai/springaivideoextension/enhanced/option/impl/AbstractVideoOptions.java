package com.springai.springaivideoextension.enhanced.option.impl;

import com.springai.springaivideoextension.common.util.JsonUtils;
import com.springai.springaivideoextension.enhanced.option.VideoOptions;

/**
 * 视频选项的抽象基类，实现了VideoOptions接口
 * 提供了视频模型的基本属性和通用方法实现
 *
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
public abstract class AbstractVideoOptions implements VideoOptions {
    /**
     * 模型唯一标识符
     */
    protected String modelId;
    
    /**
     * 模型名称
     */
    protected String modelName;
    
    /**
     * 模型描述信息
     */
    protected String modelDescription;
    
    /**
     * 获取模型唯一标识符
     *
     * @return 模型ID字符串
     */
    @Override
    public String getModelId() {
        return modelId;
    }
    
    /**
     * 获取模型名称
     *
     * @return 模型名称字符串
     */
    @Override
    public String getModelName() {
        return modelName;
    }
    
    /**
     * 获取模型描述信息
     *
     * @return 模型描述字符串
     */
    @Override
    public String getModelDescription() {
        return modelDescription;
    }
    
    /**
     * 构建JSON请求体，将当前对象序列化为JSON字符串
     *
     * @return JSON请求体字符串
     */
    @Override
    public String buildJsonBody() {
        return JsonUtils.writeValueAsString(this);
    }
}
