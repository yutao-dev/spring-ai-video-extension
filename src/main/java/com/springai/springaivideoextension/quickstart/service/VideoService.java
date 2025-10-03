package com.springai.springaivideoextension.quickstart.service;

import com.springai.springaivideoextension.common.util.ImageUtils;
import com.springai.springaivideoextension.enhanced.client.VideoClient;
import com.springai.springaivideoextension.enhanced.model.enums.VideoGenerationModel;
import com.springai.springaivideoextension.enhanced.storage.VideoStorage;
import com.springai.springaivideoextension.enhanced.trimer.response.VideoScanResponse;
import com.springai.springaivideoextension.quickstart.bean.request.GenerateVideoRequest;
import com.springai.springaivideoextension.quickstart.bean.response.GenerateVideoResponse;
import com.springai.springaivideoextension.quickstart.bean.response.VideoStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
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
        VideoClient.ParamBuilder paramBuilder = videoClient.param()
                .prompt(request.getPrompt())
                .model(request.getModel())
                // 使用paramSet方法设置视频尺寸参数
                .paramSet("videoSize", request.getVideoSize(), String.class)
                .paramSet("negativePrompt", request.getNegativePrompt(), String.class)
                .image(request.getImage())
                .paramSet("seed", request.getSeed(), Long.class);

        if (!Objects.isNull(request.getImage())) {

            String output = imageToVideo(paramBuilder, request.getImage());

            return new GenerateVideoResponse(output);
        } else {

            String output = textToVideo(paramBuilder);

            return new GenerateVideoResponse(output);
        }
    }

    /**
     * 文本转视频
     *
     * @param paramBuilder 参数构造器
     * @return 输出结果
     */
    private String textToVideo(VideoClient.ParamBuilder paramBuilder) {
        log.info("文本转视频: {}", paramBuilder);
        return paramBuilder.getOutput();
    }

    /**
     * 文本转视频
     *
     * @param paramBuilder 参数构造器
     * @param image
     * @return 输出结果
     */
    @SneakyThrows
    private String imageToVideo(VideoClient.ParamBuilder paramBuilder, String image) {
        log.info("图片转视频: {}", paramBuilder);

        File imageAsUrl = ImageUtils.createImageAsUrl(image);
        String convert = ImageUtils.convert(imageAsUrl);

        return paramBuilder
                .model(VideoGenerationModel.QWEN_IMAGE_TO_VIDEO.getModel())
                .image(convert)
                .getOutput();
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