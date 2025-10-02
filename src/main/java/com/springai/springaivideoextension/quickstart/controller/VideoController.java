package com.springai.springaivideoextension.quickstart.controller;

import com.springai.springaivideoextension.quickstart.bean.request.GenerateVideoRequest;
import com.springai.springaivideoextension.quickstart.bean.response.GenerateVideoResponse;
import com.springai.springaivideoextension.quickstart.bean.response.Result;
import com.springai.springaivideoextension.quickstart.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/2
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public Result<GenerateVideoResponse> generateVideo(@RequestBody GenerateVideoRequest request) {
        log.info("生成视频请求: {}", request);

        GenerateVideoResponse generateVideoResponse = videoService.generateVideo(request);

        return Result.success(generateVideoResponse);
    }

}
