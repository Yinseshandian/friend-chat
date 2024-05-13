package com.li.chat.param.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * @author malaka
 */

@Data
@ApiModel("Param群聊创建")
public class GroupCreateParam {

    @Length(min =1,
            max = 24,
            message = "群名长度为1-24个字符")
    private String name;

    @ApiModelProperty(value = "入群权限，0：需管理认证 1：开放加入 2：禁止加入")
    @Range(message = "入群权限代码错误",
            min = 1,
            max = 2
    )
    private Integer joinMode;

}
