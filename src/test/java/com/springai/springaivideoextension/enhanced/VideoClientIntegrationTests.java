package com.springai.springaivideoextension.enhanced;

import com.springai.springaivideoextension.enhanced.client.VideoClient;
import com.springai.springaivideoextension.enhanced.model.VideoModel;
import com.springai.springaivideoextension.enhanced.model.impl.VideoModelImpl;
import com.springai.springaivideoextension.enhanced.option.VideoOptions;
import com.springai.springaivideoextension.enhanced.option.impl.VideoOptionsImpl;
import com.springai.springaivideoextension.enhanced.storage.impl.InMemoryVideoStorage;
import com.springai.springaivideoextension.enhanced.api.VideoApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VideoClientIntegrationTests {

    @Autowired
    private VideoClient videoClient;
    
    private VideoModel videoModel;
    private InMemoryVideoStorage videoStorage;

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
        
        // 创建存储实例
        videoStorage = new InMemoryVideoStorage();
    }

    @Test
    void testVideoClientCreation() {
        VideoClient client = new VideoClient(videoModel);
        assertThat(client).isNotNull();
    }

    @Test
    void testVideoClientWithStorageCreation() {
        VideoClient client = new VideoClient(videoModel, videoStorage);
        assertThat(client).isNotNull();
    }

    @Test
    void testVideoClientParamBuilder() {
        VideoClient client = new VideoClient(videoModel, videoStorage);
        VideoClient.ParamBuilder paramBuilder = client.param();
        assertThat(paramBuilder).isNotNull();
    }

    // 注意：由于这是一个集成测试，实际的API调用需要mock或使用测试环境
    // 下面的测试需要配合mock服务器或实际的测试API端点
    /*
    @Test
    void testVideoGeneration() {
        VideoClient client = new VideoClient(videoModel, videoStorage);
        
        String result = client.param()
                .prompt("一个美丽的日落场景")
                .model("test-model")
                .image("test-image-url")
                .getOutput();
        
        assertThat(result).isNotNull();
    }
    */
}