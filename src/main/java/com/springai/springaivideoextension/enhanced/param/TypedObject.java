package com.springai.springaivideoextension.enhanced.param;

import lombok.*;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author 王玉涛
 * @version 1.0
 * @since 2025/10/3
 */
@Data
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TypedObject<T> {
    /**
     * 存储的实际值对象
     */
    private Object value;
    
    /**
     * 值对象的类型信息
     */
    private Class<T> type;

    private static final TypedObject<?> INSTANCE = new TypedObject<>(null, null);

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
        return (Objects.isNull(value) || Objects.isNull(type)) ? null : type.cast(value);
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

    /**
     * 获取一个空 TypedObject 实例
     *
     * @return 空 TypedObject 实例
     */
    public static TypedObject<?> getInstance() {
        return TypedObject.INSTANCE;
    }
}
