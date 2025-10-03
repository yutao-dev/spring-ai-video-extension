package com.springai.springaivideoextension.enhanced.client;

import com.springai.springaivideoextension.enhanced.api.VideoApi;
import com.springai.springaivideoextension.enhanced.config.VideoApiConfig;
import com.springai.springaivideoextension.enhanced.model.VideoModel;
import com.springai.springaivideoextension.enhanced.model.impl.VideoModelImpl;
import com.springai.springaivideoextension.enhanced.option.factory.VideoOptionsFactory;
import com.springai.springaivideoextension.enhanced.option.impl.HuoShanVideoOptions;
import com.springai.springaivideoextension.enhanced.option.impl.SiliconCloudVideoOptions;
import com.springai.springaivideoextension.enhanced.storage.VideoStorage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
@SpringBootTest
class VideoClientTest {

    @Resource
    private VideoApiConfig videoApiConfig;
    @Resource
    private VideoStorage videoStorage;


    @Test
    void testVideoClient() {

        List<String> apiKeys = videoApiConfig.getApiKeys();
        List<String> baseUrls = videoApiConfig.getBaseUrls();
        List<String> videoPaths = videoApiConfig.getVideoPaths();
        List<String> videoStatusPaths = videoApiConfig.getVideoStatusPaths();

        Map<String, VideoApi> videoApiMap = new HashMap<>();

        for (int i = 0; i < apiKeys.size(); i++) {
            VideoApi.Builder videoApi = VideoApi.builder()
                    .apiKey(apiKeys.get(i))
                    .baseUrl(baseUrls.get(i))
                    .videoPath(videoPaths.get(i))
                    .videoStatusPath(videoStatusPaths.get(i));
            videoApiMap.put("" + i, videoApi.build());
        }
        Assert.isTrue(!videoApiMap.isEmpty(), "没有可用的apiKey");

        Map<String, VideoModel> videoModelMap = videoApiMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                    entry -> new VideoModelImpl(entry.getValue()),
                    (oldValue, newValue) -> oldValue
                ));

        SiliconCloudVideoOptions siliconCloudVideoOptions = new SiliconCloudVideoOptions();
        siliconCloudVideoOptions.setModelId("0");
        HuoShanVideoOptions huoShanVideoOptions = new HuoShanVideoOptions();
        huoShanVideoOptions.setModelId("1");
        VideoOptionsFactory videoOptionsFactory = new VideoOptionsFactory(List.of(siliconCloudVideoOptions, huoShanVideoOptions));

        VideoClient videoClient = new VideoClient(videoModelMap.get("0"), videoStorage, videoModelMap, videoOptionsFactory);

        String output = videoClient.param()
                .modelId("0")
                .model("Wan-AI/Wan2.2-T2V-A14B")
                .prompt("生成鸟儿飞翔的视频")
                .getOutput();

        System.out.println(output);

        String output1 = videoClient.param()
                .modelId("1")
                .model("doubao-seedance-1-0-lite-t2v-250428")
                .prompt("生成鸟儿飞翔的视频")
                .getOutput();

        System.out.println(output1);
    }
}
