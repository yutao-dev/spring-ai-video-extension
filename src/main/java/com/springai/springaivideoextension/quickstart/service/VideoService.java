package com.springai.springaivideoextension.quickstart.service;

import com.springai.springaivideoextension.common.util.BeanUtils;
import com.springai.springaivideoextension.enhanced.client.VideoClient;
import com.springai.springaivideoextension.enhanced.storage.VideoStorage;
import com.springai.springaivideoextension.enhanced.trimer.response.VideoScanResponse;
import com.springai.springaivideoextension.quickstart.bean.request.GenerateVideoRequest;
import com.springai.springaivideoextension.quickstart.bean.response.GenerateVideoResponse;
import com.springai.springaivideoextension.quickstart.bean.response.VideoStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
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
    private final VideoStorage videoStorage;

    /**
     * 生成视频
     * @param request 请求参数
     * @return 响应结果
     */
    public GenerateVideoResponse generateVideo(GenerateVideoRequest request) {
        Assert.notNull(request.getPrompt(), "提示词不能为空");

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

    /**
     * 获取视频状态
     * @param requestId 请求ID
     * @return 状态响应
     */
    public VideoStatusResponse getVideoStatus(String requestId) {
        VideoScanResponse scanResponse = (VideoScanResponse) videoStorage.findVideoById(requestId);

        log.info("获取视频状态: {}", requestId);

        VideoScanResponse.Results results = Objects.isNull(scanResponse) ? null : scanResponse.getResults();

        return new VideoStatusResponse(requestId, Objects.isNull(results) ? null : results.getVideos().get(0).getUrl());
    }
}
