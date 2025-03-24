package com.li.chat.vo;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @author malaka
 */
@ApiModel(value = "VO申请信息")
@Data
@Builder
public class ApplyVo {

    private Long id;

    private Long fromId;

    private Long toId;

    private String message;

    private String remark;

    private Integer status; // 申请状态 0未处理 1同意 2拒绝

    private String nickname;

    private String username;

    private String avatar;

    private String sex;

}
