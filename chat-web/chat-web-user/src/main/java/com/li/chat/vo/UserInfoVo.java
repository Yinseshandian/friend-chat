package com.li.chat.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author malaka
 */
@ApiModel(value = "VO用户信息")
@Data
@Builder
public class UserInfoVo {

    @ApiModelProperty(value = "用户id", example = "10000")
    private Long id;

    @ApiModelProperty(value = "账号", example = "malaka")
    private String username;

    @ApiModelProperty(value = "昵称", example = "马拉卡")
    private String nickname;

    @ApiModelProperty(value = "头像", example = "default_avatar.jpg")
    private String avatar;

    @ApiModelProperty(value = "签名", example = "今天吃什么")
    private String signature;

    @ApiModelProperty(value = "性别 0：保密，1：男，2：女", example = "1")
    private Integer sex;

    @ApiModelProperty(value = "账号状态 0：正常，1：冻结，2：注销", example = "0")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer status;

}
