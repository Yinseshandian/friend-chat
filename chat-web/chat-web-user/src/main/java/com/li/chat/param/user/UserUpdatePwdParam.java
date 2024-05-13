package com.li.chat.param.user;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * @author malaka
 */
@ApiModel("Param用户更改密码")
@Data
public class UserUpdatePwdParam {

    @ApiModelProperty(value = "旧密码", required = true)
    @NotNull(message = "请输入旧密码")
    @Pattern(message = "密码由8-16个字母、数字、字符~!@#$%^&*.-_?组成",
            regexp = "^([a-zA-Z0-9~!@#$%^&*.-_?]{8,16})+$")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @NotNull(message = "请输入新密码")
    @Pattern(message = "密码由8-16个字母、数字、字符~!@#$%^&*.-_?组成",
            regexp = "^([a-zA-Z0-9~!@#$%^&*.-_?]{8,16})+$")
    private String newPassword;

}
