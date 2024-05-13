package com.li.chat.param.user;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据类型
 */
@Data
@ApiModel(value = "公用返回值222", description = "公用返回值222")
@ApiResponses({
        @ApiResponse(code = 0, message = "msg")
})
@Slf4j
public class TestParam <T>{

    @ApiModelProperty(value = "响应码", example = "200")
    private int code;
    @ApiModelProperty(value = "响应消息", example = "success")
    private String msg;
    @ApiModelProperty(value = "响应数据")
    private T data;


}
