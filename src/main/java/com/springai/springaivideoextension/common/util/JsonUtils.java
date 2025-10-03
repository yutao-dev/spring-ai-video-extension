package com.springai.springaivideoextension.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * JSON工具类，提供JSON序列化和反序列化功能
 *
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
@Slf4j
public class JsonUtils {
    
    /**
     * 全局共享的ObjectMapper实例，用于JSON操作
     */
    private static final ObjectMapper OBJECT_MAPPER;
    
    static {
        OBJECT_MAPPER = new ObjectMapper();
    }
    
    /**
     * 私有构造函数，防止实例化工具类
     */
    private JsonUtils() {}
    
    /**
     * 将对象序列化为JSON字符串
     *
     * @param obj 需要序列化的对象
     * @return 序列化后的JSON字符串，如果序列化失败则返回"{}"
     */
    public static String writeValueAsString(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("序列化对象失败: {}", obj, e);
            return "{}";
        }
    }

    public static <T> Map<String, T> readValueToMap(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, T>>() {});
        } catch (JsonProcessingException e) {
            log.error("反序列化JSON失败: {}", json, e);
            return null;
        }
    }
}
