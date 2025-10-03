package com.springai.springaivideoextension.jsonbody;

import com.springai.springaivideoextension.enhanced.option.VideoOptions;
import com.springai.springaivideoextension.enhanced.option.impl.HuoShanVideoOptions;
import com.springai.springaivideoextension.enhanced.option.impl.SiliconCloudVideoOptions;
import com.springai.springaivideoextension.enhanced.param.TypedObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
class VideoOptionsJsonBodyTest {

    /**
     * 测试火山视频选项配置的JSON构建功能
     * 该测试用例验证HuoShanVideoOptions能否正确处理参数并生成符合预期的JSON请求体
     */
    @Test
    void testHuoShanVideoOptions() {
        // 初始化参数映射表，用于存储视频生成所需的各种配置参数
        Map<String, TypedObject<?>> params = new HashMap<>();
        
        // 设置模型基本属性参数
        params.put("modelId", TypedObject.valueOf("huoshan", String.class));           // 模型ID
        params.put("modelName", TypedObject.valueOf("火山视频", String.class));         // 模型名称
        params.put("modelDescription", TypedObject.valueOf("火山视频生成视频", String.class)); // 模型描述
        
        // 设置视频生成核心参数
        params.put("prompt", TypedObject.valueOf("一个boy在打篮球", String.class));      // 视频生成提示词
        params.put("model", TypedObject.valueOf("video-generator-v1", String.class));   // 使用的具体模型版本
        params.put("image", TypedObject.valueOf("https://picsum.photos/200/300", String.class)); // 参考图像URL
        
        // 创建并初始化火山视频选项实例
        VideoOptions videoOptions = new HuoShanVideoOptions();
        videoOptions = videoOptions.fromParameters(params);  // 通过参数配置视频选项

        // 输出构建的JSON请求体，用于验证格式是否正确
        System.out.println(videoOptions.buildJsonBody());
    }
    
    /**
     * 测试硅基流动视频选项配置的JSON构建功能
     * 该测试用例验证SiliconCloudVideoOptions能否正确处理参数并生成符合预期的JSON请求体
     */
    @Test
    void testSiliconCloudVideoOptions() {
        // 初始化参数映射表，用于存储视频生成所需的各种配置参数
        Map<String, TypedObject<?>> params = new HashMap<>();
        
        // 设置模型基本属性参数
        params.put("modelId", TypedObject.valueOf("silongcloud", String.class));       // 模型ID
        params.put("modelName", TypedObject.valueOf("硅基流动", String.class));         // 模型名称
        params.put("modelDescription", TypedObject.valueOf("思龙云视频生成视频", String.class)); // 模型描述
        
        // 设置视频生成核心参数
        params.put("prompt", TypedObject.valueOf("一个boy在打篮球", String.class));      // 视频生成提示词
        params.put("model", TypedObject.valueOf("video-generator-v1", String.class));   // 使用的具体模型版本
        params.put("image", TypedObject.valueOf("https://picsum.photos/200/300", String.class)); // 参考图像URL
        params.put("seed", TypedObject.valueOf(1234567890L, Long.class));
        params.put("imageSize", TypedObject.valueOf("1024x1024", String.class));
        params.put("negativePrompt", TypedObject.valueOf("bad, ugly, low quality", String.class));
        
        // 创建并初始化硅基流动视频选项实例
        VideoOptions videoOptions = new SiliconCloudVideoOptions();
        videoOptions = videoOptions.fromParameters(params);  // 通过参数配置视频选项
        
        // 输出构建的JSON请求体，用于验证格式是否正确
        System.out.println(videoOptions.buildJsonBody());
    }

    /**
     * 测试视频选项构建性能
     * 该测试用例用于测试视频选项构建的效率，并计算平均耗时
     */
    @Test
    void testStress() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            // 创建并初始化硅基流动视频选项实例
            testSiliconCloudVideoOptions();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");

        long startTime2 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            // 创建并初始化火山视频选项实例
            testHuoShanVideoOptions();
        }
        long endTime2 = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime2 - startTime2) + "ms");

        System.out.println("平均耗时: SiliconCloud: " + (endTime - startTime) / 100000.0 + "ms, HuoShan: " + (endTime2 - startTime2) / 100000.0 + "ms" );
    }
}
