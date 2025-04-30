package com.li.chat.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author malaka
 */

@ApiModel(value = "VO群申请信息")
@Data
@Builder
public class GroupApplyVo {

    private Long id;

    private Long groupId;

    private Long inviteUserId; // 群成员邀请id

    private String inviteUserName; // 群成员邀请名

    private Long userId;

    private Long processedBy; // 处理人

    private String message;

    private Integer type; // 类型   1群成员邀请

    private Integer status; // 申请状态 0未处理 1同意 2拒绝

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String nickname;

    private String username;

    private String avatar;

    private Integer sex;

    private String groupName;

}
