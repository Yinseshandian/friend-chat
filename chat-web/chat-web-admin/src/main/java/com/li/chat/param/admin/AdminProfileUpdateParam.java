package com.li.chat.param.admin;

import lombok.Data;

import javax.validation.constraints.Email;

/**
 * @author malaka
 */
@Data
public class AdminProfileUpdateParam {

    private String nickname;

    private String avatar;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String mobile;

}
