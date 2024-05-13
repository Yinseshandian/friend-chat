package com.li.chat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;

/**
 * @author malaka
 */
@Data
@ApiModel(description = "响应对象")
public class TestResult<T> extends HashMap<String, Object> {

    @ApiModelProperty(value = "状态码", example = "200")
    private int code;

    @ApiModelProperty(value = "返回消息", example = "操作成功")
    private String msg;

    @ApiModelProperty(value = "数据对象")
    private T data;

    // 其他字段和方法...
}


