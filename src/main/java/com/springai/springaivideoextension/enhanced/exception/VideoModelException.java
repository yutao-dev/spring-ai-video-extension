package com.springai.springaivideoextension.enhanced.exception;

import lombok.AllArgsConstructor;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
@AllArgsConstructor
public class VideoModelException extends RuntimeException {
    public VideoModelException(String message) {
        super(message);
    }
}
