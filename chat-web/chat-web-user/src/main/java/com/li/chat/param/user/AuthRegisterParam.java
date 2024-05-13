package com.li.chat.param.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author malaka
 */
@ApiModel("Param用户注册")
@Data
public class AuthRegisterParam {

    @ApiModelProperty(value = "验证码" ,
            required = true)
    @NotNull(message = "请输入验证码")
    private String code;

    @ApiModelProperty(
            value = "账号 5-20个字母、数字、字符-_组成，以字母开头",
            example = "malaka",
            required = true
    )
    @NotNull(message = "请输入账号")
    @Pattern(message = "账号由5-20个字母、数字、字符-_组成，以字母开头",
            regexp = "^[a-zA-Z]([-_a-zA-Z0-9]{5,19})+$")
    private String username;

    @ApiModelProperty(
            value = "密码 8-16个字母、数字、字符~!@#$%^&*.-_?组成",
            example = "12345678",
            required = true
    )
    @NotNull(message = "请输入密码")
    @Pattern(message = "密码由8-16个字母、数字、字符~!@#$%^&*.-_?组成",
            regexp = "^([a-zA-Z0-9~!@#$%^&*.-_?]{8,16})+$")
    private String password;

}
