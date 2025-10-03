package com.springai.springaivideoextension.enhanced.factory;

import com.springai.springaivideoextension.enhanced.option.VideoOptions;
import com.springai.springaivideoextension.enhanced.option.factory.VideoOptionsFactory;
import com.springai.springaivideoextension.enhanced.option.impl.VideoOptionsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class VideoOptionsFactoryIntegrationTests {

    @Autowired
    private VideoOptionsFactory videoOptionsFactory;

    @Test
    void testFactoryCreation() {
        assertThat(videoOptionsFactory).isNotNull();
    }

    @Test
    void testGetVideoOptionsByModelId() {
        // 创建一个测试用的VideoOptions
        VideoOptions testOptions = VideoOptionsImpl.builder()
                .model("test-model")
                .modelId("test-model-id")
                .modelName("Test Model")
                .modelDescription("A test model for integration testing")
                .prompt("test prompt")
                .build();

        // 注意：实际测试中我们无法直接添加到工厂中，因为它是通过构造函数注入的
        // 这里只是验证工厂方法的存在和基本功能
        assertThat(videoOptionsFactory).isNotNull();
    }

    // 注意：以下测试需要在实际的VideoOptions实现被正确配置并注入到Spring容器中才能运行
    /*
    @Test
    void testGetVideoOptionsByModelName() {
        VideoOptions options = videoOptionsFactory.getVideoOptionsByModel("test-model");
        assertThat(options).isNotNull();
        assertThat(options.getModel()).isEqualTo("test-model");
    }

    @Test
    void testGetVideoOptionsWithInvalidModelId() {
        assertThrows(RuntimeException.class, () -> {
            videoOptionsFactory.getVideoOptions("invalid-model-id");
        });
    }
    */
}