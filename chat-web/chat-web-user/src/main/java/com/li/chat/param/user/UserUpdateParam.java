package com.li.chat.param.user;

import com.li.chat.common.enums.UserEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author malaka
 */
@Data
@ApiModel("Param用户更新信息")
public class UserUpdateParam {

    @ApiModelProperty(value = "昵称 1-24个字符")
    @Length(message = "昵称长度为1-24个字符",
            min = 1,
            max = 24
    )
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "签名")
    private String signature;

    @ApiModelProperty(value = "性别，0：保密，1：男，2：女")
    @Range(message = "性别格式错误",
            min = 0,
            max = 2
    )
    private Integer sex;

}
