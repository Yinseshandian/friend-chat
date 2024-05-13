package com.li.chat.param.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author malaka
 */
@ApiModel(value = "Param群聊加入邀请")
@Data
public class GroupInviteParam {

    @ApiModelProperty(value = "对方id", required = true)
    @NotNull(message = "群号不能为空")
    private Long groupId;

    @NotEmpty(message = "userIdList不可为空")
    @ApiModelProperty(value = "用户id列表", required = true)
    private List<Long> userIdList;

}
