package com.li.chat.vo.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author malaka
 */
@ApiModel(value = "群组申请信息VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupApplyVO {

    @ApiModelProperty(value = "申请ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "群组ID", example = "100")
    private Long groupId;

    @ApiModelProperty(value = "群组名称", example = "技术交流群")
    private String groupName;

    @ApiModelProperty(value = "群组头像", example = "group-avatar.jpg")
    private String groupPhoto;

    @ApiModelProperty(value = "申请用户ID", example = "1001")
    private Long userId;

    @ApiModelProperty(value = "申请用户名", example = "zhangsan")
    private String username;

    @ApiModelProperty(value = "申请用户头像", example = "avatar.jpg")
    private String userAvatar;

    @ApiModelProperty(value = "邀请人ID", example = "1002")
    private Long inviteUserId;

    @ApiModelProperty(value = "邀请人用户名", example = "lisi")
    private String inviteUsername;

    @ApiModelProperty(value = "邀请人头像", example = "avatar.jpg")
    private String inviteUserAvatar;

    @ApiModelProperty(value = "处理人ID", example = "1003")
    private Long processedBy;

    @ApiModelProperty(value = "处理人用户名", example = "wangwu")
    private String processedByUsername;

    @ApiModelProperty(value = "处理人头像", example = "avatar.jpg")
    private String processedByAvatar;

    @ApiModelProperty(value = "申请消息", example = "我想加入该群")
    private String message;

    @ApiModelProperty(value = "申请类型(0:用户申请,1:群成员邀请)", example = "0")
    private Integer type;

    @ApiModelProperty(value = "申请状态(0:未处理,1:同意,2:拒绝)", example = "0")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}