package com.springai.springaivideoextension.quickstart.bean.request;

import lombok.Data;

/**
 * 视频生成请求参数类
 * 用于封装AI视频生成接口所需的请求参数
 *
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/2
 */
@Data
public class GenerateVideoRequest {
    /**
     * 视频生成的提示词
     * 描述希望生成的视频内容
     */
    private String prompt;
    
    /**
     * 使用的AI模型名称
     * 指定用于视频生成的具体模型
     */
    private String model;
    
    /**
     * 视频尺寸规格
     * 定义生成视频的分辨率大小
     */
    private String videoSize;
    
    /**
     * 负面提示词
     * 描述不希望在视频中出现的内容
     */
    private String negativePrompt;
    
    /**
     * 参考图像
     * 提供用于视频生成的参考图像数据
     */
    private String image;
    
    /**
     * 随机种子
     * 用于控制视频生成的随机性，相同种子可产生相似结果
     */
    private Long seed;
}
