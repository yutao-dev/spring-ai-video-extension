package com.springai.springaivideoextension.enhanced.config;

import com.springai.springaivideoextension.enhanced.api.VideoApi;
import com.springai.springaivideoextension.enhanced.storage.VideoStorage;
import com.springai.springaivideoextension.enhanced.storage.impl.InMemoryVideoStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * 创建视频存储服务Bean
     * 
     * 提供视频数据的存储功能，默认使用内存存储实现。
     * 后续可根据需求替换为其他持久化存储方案。
     * 
     * @return 视频存储服务实例
     */
    @Bean
    public VideoStorage videoStorage() {
        return new InMemoryVideoStorage();
    }
}
