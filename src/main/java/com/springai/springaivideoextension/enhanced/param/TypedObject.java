package com.springai.springaivideoextension.enhanced.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
@Data
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class TypedObject<T> {
    /**
     * 存储的实际值对象
     */
    private final Object value;
    
    /**
     * 值对象的类型信息
     */
    private final Class<T> type;

    /**
     * 创建 TypedObject 实例的静态工厂方法
     *
     * @param value 实际值，不能为 null
     * @param type  值的类型，不能为 null
     * @param <T>   泛型类型参数
     * @return TypedObject 实例
     * @throws IllegalArgumentException 当 value 或 type 为 null 时抛出
     */
    public static <T> TypedObject<T> valueOf(Object value, Class<T> type) {
        Assert.notNull(value, "value 不能为 null");
        Assert.notNull(type, "type 不能为 null");
        return new TypedObject<>(value, type);
    }

    /**
     * 获取类型转换后的值
     *
     * @return 经过类型转换的值
     * @throws ClassCastException 当值无法转换为目标类型时抛出
     */
    public T getValue() {
        return type.cast(value);
    }

    /**
     * 检查当前对象的类型是否与目标类型兼容
     *
     * @param targetType 目标类型
     * @return 如果当前类型是目标类型的子类型或相同类型则返回 true，否则返回 false
     */
    public boolean isTypeOf(Class<?> targetType) {
        return targetType != null && targetType.isAssignableFrom(this.type);
    }
}
