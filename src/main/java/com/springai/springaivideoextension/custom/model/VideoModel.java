package com.ai.springaidemo02.ai.video.custom.model;

import com.ai.springaidemo02.ai.video.custom.model.request.VideoPrompt;
import com.ai.springaidemo02.ai.video.custom.model.response.VideoResponse;
import org.springframework.ai.model.Model;

/**
 * 注意：这里有两个注意点，VideoPrompt、VideoApi.VideoResponse都需要实现ModelRequest、ModelResponse
 * 所以这里我们逐步解决
 * @author 王玉涛
 * @version 1.0
 * @since 2025/9/29
 */
public interface VideoModel extends Model<VideoPrompt, VideoResponse> {
}
