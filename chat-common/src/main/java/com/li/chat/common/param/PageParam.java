package com.li.chat.common.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author malaka
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageParam {
    private int pageNum = 1;
    private int pageSize = 10;
}
