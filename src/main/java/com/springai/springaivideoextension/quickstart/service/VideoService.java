package com.springai.springaivideoextension.quickstart.service;

import com.springai.springaivideoextension.enhanced.client.VideoClient;
import com.springai.springaivideoextension.quickstart.bean.request.GenerateVideoRequest;
import com.springai.springaivideoextension.quickstart.bean.response.GenerateVideoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoClient videoClient;

    /**
     * 生成视频
     * @param request 请求参数
     * @return 响应结果
     */
    public GenerateVideoResponse generateVideo(GenerateVideoRequest request) {
        Assert.notNull(request.getPrompt(), "提示词不能为空");
        Assert.isTrue(
    !Objects.isNull(request.getImage()) && "Wan-AI/Wan2.2-T2V-A14B".equals(request.getModel()),
    "该模型不支持图片生成视频！"
        );

        // 这里所有的参数都会进行默认覆盖
        String requestId = videoClient.param()
                .prompt(request.getPrompt())
                .model(request.getModel())
                .imageSize(request.getVideoSize())
                .negativePrompt(request.getNegativePrompt())
                .image(request.getImage())
                .seed(request.getSeed())
                .getOutput();

        return new GenerateVideoResponse(requestId);
    }
}
