package com.li.chat.vo.router;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author malaka
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaVO {
    /**
     * 标题
     */
    private String title;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否不缓存
     */
    private Boolean noCache;
    private Boolean keepAlive;
}