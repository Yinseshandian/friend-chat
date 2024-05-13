package com.li.chat.vo;

import com.li.chat.common.utils.HttpStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author malaka
 * 公共返回对象
 */
@ApiModel("malaka")
@ApiResponses({
        @ApiResponse(code = 0, message = "msg")
})
public class ResultData {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(
            value = "状态码",
            name = "code"
    )
    /** 状态码 */
    public static final String CODE_TAG = "code";

    @ApiModelProperty(
            value = "返回信息",
            name = "msg"
    )
    /** 返回信息 */
    public static final String MSG_TAG = "msg";

    @ApiModelProperty(
            value = "数据对象",
            name = "data"
    )
    /** 数据对象 */
    public static final String DATA_TAG = "data";


}
