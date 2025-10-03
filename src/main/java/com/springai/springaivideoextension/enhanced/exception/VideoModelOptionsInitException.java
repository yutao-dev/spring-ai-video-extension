package com.springai.springaivideoextension.enhanced.exception;


/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
public class VideoModelOptionsInitException extends VideoModelException {
    public VideoModelOptionsInitException() {
        super("模型初始化失败！");
    }

    public VideoModelOptionsInitException(String message) {
        super(message);
    }
}
