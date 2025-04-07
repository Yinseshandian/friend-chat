package com.li.chat.param.admin;

/**
 * @author malaka
 */

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AdminUpdateParam {

    @NotNull(message = "管理员ID不能为空")
    private Long id;

    private String nickname;

    private String avatar;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String mobile;

    private List<Long> roleIds;
}