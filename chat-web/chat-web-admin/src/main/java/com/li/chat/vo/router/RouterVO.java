package com.li.chat.vo.router;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author malaka
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouterVO {
    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由
     */
    private Boolean hidden;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 总是显示
     */
    private Boolean alwaysShow;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 路由meta信息
     */
    private MetaVO meta;

    /**
     * 子路由
     */
    private List<RouterVO> children;
}