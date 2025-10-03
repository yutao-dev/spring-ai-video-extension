package com.springai.springaivideoextension.enhanced.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.video")
public class VideoApiConfig {
    private List<String> apiKeys;
    private List<String> baseUrls;
    private List<String> videoPaths;
    private List<String> videoStatusPaths;
}
