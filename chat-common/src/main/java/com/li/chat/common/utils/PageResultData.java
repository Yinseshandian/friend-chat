package com.li.chat.common.utils;

import lombok.*;

import java.util.Collection;
import java.util.List;

/**
 * @author malaka
 * 分页数据
 */
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PageResultData<T> {

    // 总条数
    private Long total;
    // 分页大小
    private Integer pageSize;
    // 当前页
    private Integer pageNum;
    // 数据
    private Collection<T> rows;

    private Integer code;

}
