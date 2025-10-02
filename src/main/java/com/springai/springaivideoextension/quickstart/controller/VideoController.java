package com.springai.springaivideoextension.quickstart.controller;

import com.springai.springaivideoextension.quickstart.bean.request.GenerateVideoRequest;
import com.springai.springaivideoextension.quickstart.bean.response.GenerateVideoResponse;
import com.springai.springaivideoextension.quickstart.bean.response.Result;
import com.springai.springaivideoextension.quickstart.bean.response.VideoStatusResponse;
import com.springai.springaivideoextension.quickstart.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 视频生成控制器
 * 提供视频生成和状态查询的REST API接口
 *
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

    /**
     * 根据提示生成视频
     * 
     * @param request 包含视频生成参数的请求对象
     * @return 返回视频生成结果，包含请求ID等信息
     */
    @PostMapping
    public Result<GenerateVideoResponse> generateVideo(@RequestBody GenerateVideoRequest request) {
        log.info("生成视频请求: {}", request);

        GenerateVideoResponse generateVideoResponse = videoService.generateVideo(request);

        return Result.success(generateVideoResponse);
    }

    /**
     * 查询视频生成状态
     * 
     * @param requestId 视频生成请求的唯一标识符
     * @return 返回视频当前的生成状态信息
     */
    @GetMapping("/status/{requestId}")
    public Result<VideoStatusResponse> getVideoStatus(@PathVariable String requestId) {
        log.info("查询视频状态请求: {}", requestId);
        
        VideoStatusResponse response = videoService.getVideoStatus(requestId);
        
        return Result.success(response);
    }

}
