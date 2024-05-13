package com.li.chat.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author malaka
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplyDTO {

    private Long id;

    private Long fromId;

    private Long toId;

    private String message;

    private String remark;

    private Integer status; // 申请状态 0未处理 1同意 2拒绝

}
