package com.li.chat.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author malaka
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupApplyDTO {

    private Long id;

    private Long groupId;

    private Long inviteUserId; // 群成员邀请id

    private Long userId;

    private Long processedBy; // 处理人

    private String message;

    private Integer type; // 类型   1群成员邀请

    private Integer status; // 申请状态 0未处理 1同意 2拒绝

    private LocalDateTime createTime;

}
