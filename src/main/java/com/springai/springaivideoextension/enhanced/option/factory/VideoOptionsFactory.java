package com.springai.springaivideoextension.enhanced.option.factory;

import com.springai.springaivideoextension.enhanced.exception.VideoModelOptionsInitException;
import com.springai.springaivideoextension.enhanced.exception.VideoModelOptionsNotFoundException;
import com.springai.springaivideoextension.enhanced.option.VideoOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
