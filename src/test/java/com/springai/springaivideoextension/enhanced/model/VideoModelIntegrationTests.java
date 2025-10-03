package com.springai.springaivideoextension.enhanced.model;

import com.springai.springaivideoextension.enhanced.api.VideoApi;
import com.springai.springaivideoextension.enhanced.model.impl.VideoModelImpl;
import com.springai.springaivideoextension.enhanced.option.impl.VideoOptionsImpl;
import com.springai.springaivideoextension.enhanced.model.request.VideoPrompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VideoModelIntegrationTests {

    private VideoModel videoModel;

    @BeforeEach
    void setUp() {
        // 创建模拟的API密钥
        ApiKey apiKey = new SimpleApiKey("test-api-key");
        
        // 创建VideoApi实例
        VideoApi videoApi = VideoApi.builder()
                .baseUrl("https://api.openai.com")
                .apiKey(apiKey)
                .videoPath("v1/video/submit")
                .videoStatusPath("v1/video/status")
                .restClientBuilder(RestClient.builder())
                .build();
        
        // 创建VideoModel实例
        videoModel = new VideoModelImpl(videoApi);
    }

    @Test
    void testVideoModelCreation() {
        assertThat(videoModel).isNotNull();
    }

    @Test
    void testVideoModelWithDefaultOptions() {
        VideoOptionsImpl defaultOptions = VideoOptionsImpl.builder()
                .model("test-model")
                .prompt("test prompt")
                .build();
        
        ApiKey apiKey = new SimpleApiKey("test-api-key");
        VideoApi videoApi = VideoApi.builder()
                .baseUrl("https://api.openai.com")
                .apiKey(apiKey)
                .videoPath("v1/video/submit")
                .videoStatusPath("v1/video/status")
                .restClientBuilder(RestClient.builder())
                .build();
        
        VideoModel modelWithDefaults = new VideoModelImpl(videoApi, defaultOptions);
        assertThat(modelWithDefaults).isNotNull();
    }

    // 注意：实际的API调用需要mock或使用测试环境
    /*
    @Test
    void testVideoModelCall() {
        VideoOptionsImpl options = VideoOptionsImpl.builder()
                .model("test-model")
                .prompt("生成一个关于科技的视频")
                .build();
        
        VideoPrompt prompt = new VideoPrompt("生成一个关于科技的视频", options);
        VideoResponse response = videoModel.call(prompt);
        
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
    }
    */
}