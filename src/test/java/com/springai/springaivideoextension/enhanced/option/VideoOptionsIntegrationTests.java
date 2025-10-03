package com.springai.springaivideoextension.enhanced.option;

import com.springai.springaivideoextension.enhanced.option.impl.VideoOptionsImpl;
import com.springai.springaivideoextension.enhanced.param.TypedObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VideoOptionsIntegrationTests {

    @Test
    void testVideoOptionsBuilder() {
        VideoOptions options = VideoOptionsImpl.builder()
                .prompt("生成一个科技视频")
                .model("test-model")
                .image("test-image-url")
                .build();

        assertThat(options).isNotNull();
        assertThat(options.getPrompt()).isEqualTo("生成一个科技视频");
        assertThat(options.getModel()).isEqualTo("test-model");
        assertThat(options.getImage()).isEqualTo("test-image-url");
    }

    @Test
    void testVideoOptionsWithParameters() {
        Map<String, TypedObject<?>> params = new HashMap<>();
        params.put("testParam", TypedObject.valueOf("testValue", String.class));

        VideoOptions options = VideoOptionsImpl.builder()
                .prompt("测试参数")
                .model("test-model")
                .image("test-image")
                .build();

        // 使用fromParameters创建新实例
        VideoOptions newOptions = options.fromParameters(params);
        
        assertThat(newOptions).isNotNull();
        assertThat(newOptions.getAllParameters()).containsKey("testParam");
    }

    @Test
    void testVideoOptionsJsonBody() {
        VideoOptions options = VideoOptionsImpl.builder()
                .prompt("生成一个科技视频")
                .model("test-model")
                .build();

        String jsonBody = options.buildJsonBody();
        assertThat(jsonBody).isNotNull();
        assertThat(jsonBody).contains("生成一个科技视频");
        assertThat(jsonBody).contains("test-model");
    }
}