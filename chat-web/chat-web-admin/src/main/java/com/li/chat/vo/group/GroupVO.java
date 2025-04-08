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
@ApiModel(value = "群组信息VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupVO {

    @ApiModelProperty(value = "群组ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "群组名称", example = "技术交流群")
    private String name;

    @ApiModelProperty(value = "群组头像", example = "group_avatar.jpg")
    private String photo;

    @ApiModelProperty(value = "群组介绍", example = "这是一个技术交流群")
    private String introduction;

    @ApiModelProperty(value = "群组最大成员数", example = "500")
    private Integer memberSize;

    @ApiModelProperty(value = "当前成员数", example = "120")
    private Integer memberNum;

    @ApiModelProperty(value = "群主ID", example = "10000")
    private Long holderUserId;

    @ApiModelProperty(value = "群主用户名", example = "admin")
    private String holderUsername;

    @ApiModelProperty(value = "群主头像", example = "avatar.jpg")
    private String holderAvatar;

    @ApiModelProperty(value = "加入方式(0: 自由加入, 1: 需要审核)", example = "1")
    private Integer joinMode;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}