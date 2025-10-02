package com.springai.springaivideoextension.quickstart.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 视频状态响应类，用于封装视频处理结果的响应数据
 * 
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/2
 */
@Data
@AllArgsConstructor
public class VideoStatusResponse {
    
    /**
     * 请求唯一标识符，用于追踪视频处理请求
     */
    private String requestId;
    
    /**
     * 视频访问地址，处理完成后可访问的视频URL
     */
    private String videoUrl;
}
