package com.li.chat.common.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

public class BeanCopyUtils {

    private BeanCopyUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 对象属性复制，适用于普通对象
     *
     * @param source 源对象
     * @param clazz 目标对象类型
     * @param <T> 目标对象泛型
     * @return 目标对象实例
     */
    public static <T> T copyBean(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }

        T result;
        try {
            result = clazz.newInstance();
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            throw new RuntimeException("Bean复制失败", e);
        }
        return result;
    }

    /**
     * 集合属性复制，适用于集合对象
     *
     * @param sourceList 源集合
     * @param clazz 目标对象类型
     * @param <T> 目标对象泛型
     * @param <S> 源对象泛型
     * @return 目标对象集合
     */
    public static <T, S> List<T> copyBeanList(Collection<S> sourceList, Class<T> clazz) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }

        return sourceList.stream()
                .map(source -> copyBean(source, clazz))
                .collect(Collectors.toList());
    }

    /**
     * 带自定义转换函数的集合复制
     *
     * @param sourceList 源集合
     * @param mapper 转换函数
     * @param <T> 目标对象泛型
     * @param <S> 源对象泛型
     * @return 目标对象集合
     */
    public static <T, S> List<T> copyListWithConvert(Collection<S> sourceList, Function<S, T> mapper) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }

        return sourceList.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}