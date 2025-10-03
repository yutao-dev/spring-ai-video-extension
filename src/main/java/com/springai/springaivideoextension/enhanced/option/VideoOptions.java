package com.springai.springaivideoextension.enhanced.option;

import com.springai.springaivideoextension.enhanced.param.TypedObject;
import org.springframework.ai.model.ModelOptions;

import java.util.Map;

/**
 * 视频生成选项接口
 * 该接口定义了视频生成所需的各种配置选项，继承自Spring AI的ModelOptions接口，
 * 为视频生成模型提供统一的参数配置标准。
 *
 * @author 王玉涛
 * @version 1.0
 * @since 2025/9/29
 */
public interface VideoOptions extends ModelOptions {

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

    /**
     * 获取视频生成的主要提示词
     * 用于描述期望生成的视频内容
     *
     * @return 提示词字符串
     */
    String getPrompt();

    /**
     * 获取使用的视频生成模型名称
     *
     * @return 模型名称
     */
    String getModel();

    /**
     * 获取参考图像路径或URL
     * 用于基于图像生成视频内容
     *
     * @return 图像路径或URL字符串
     */
    String getImage();

    /**
     * 获取所有参数
     *
     * @return 参数映射关系
     */
    Map<String, TypedObject<?>> getAllParameters();

    /**
     * 构建JSON请求体
     *
     * @return JSON请求体字符串
     */
    String buildJsonBody();
}
