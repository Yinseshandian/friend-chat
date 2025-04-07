package com.li.chat.annotation;

import java.lang.annotation.*;

/**
 * @author malaka
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    /**
     * 需要的权限编码
     */
    String value();

    /**
     * 权限验证失败时的错误信息
     */
    String message() default "没有访问权限";
}