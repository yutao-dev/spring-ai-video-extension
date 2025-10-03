package com.springai.springaivideoextension.enhanced.exception;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
public class VideoModelOptionsNotFoundException extends VideoModelException {
    public VideoModelOptionsNotFoundException() {
        super("未找到模型参数！");
    }

    public VideoModelOptionsNotFoundException(String message) {
        super(message);
    }
}
