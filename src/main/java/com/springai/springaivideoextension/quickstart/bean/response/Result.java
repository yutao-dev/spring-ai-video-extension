package com.springai.springaivideoextension.quickstart.bean.response;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用返回结果封装类
 *
 * @author 王玉涛
 * @param <T> 返回数据的泛型类型
 * @version 1.0
 * @since 2025/6/12
 */
@Data
@Slf4j
public class Result<T> {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 请求追踪ID
     */
    private String traceId;

    /**
     * 成功返回结果
     * @param data 返回数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.data = data;
        result.message = "success";
        return result;
    }

    /**
     * 成功返回结果
     * @return Result对象
     */
    public static Result<String> success() {
        return success(null);
    }

    /**
     * 失败返回结果
     * @param code 错误码
     * @param message 错误信息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}