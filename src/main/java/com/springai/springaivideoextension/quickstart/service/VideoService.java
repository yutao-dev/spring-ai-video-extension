package com.springai.springaivideoextension.quickstart.service;

import com.springai.springaivideoextension.enhanced.client.VideoClient;
import com.springai.springaivideoextension.quickstart.bean.request.GenerateVideoRequest;
import com.springai.springaivideoextension.quickstart.bean.response.GenerateVideoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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


    }
}
