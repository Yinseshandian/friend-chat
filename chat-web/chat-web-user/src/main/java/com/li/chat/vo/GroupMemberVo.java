package com.li.chat.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author malaka
 */
@ApiModel(value = "VO群成员信息")
@Data
@Builder
public class GroupMemberVo {

    @ApiModelProperty(value = "群成员id", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户id", example = "10000")
    private Long userId;

    @ApiModelProperty(value = "备注", example = "test")
    private String remark;

    @ApiModelProperty(value = "类型 0成员，1管理员，2群主", example = "0")
    private Integer type;

    @ApiModelProperty(value = "用户账号", example = "malaka")
    private String username;

    @ApiModelProperty(value = "用户昵称", example = "马拉卡")
    private String nickname;

    @ApiModelProperty(value = "头像", example = "default_avatar.jpg")
    private String avatar;


}
