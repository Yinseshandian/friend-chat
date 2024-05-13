package com.li.chat.common.utils;

import com.li.chat.common.enums.WebErrorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author malaka
 * 公共返回对象
 */
@ApiModel("公用返回值")
@ApiResponses({
        @ApiResponse(code = 0, message = "msg")
})
public class ResultData<T> extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    // @ApiModelProperty(
    //         value = "状态码",
    //         name = "code"
    // )
    /** 状态码 */
    public static final String CODE_TAG = "code";

    // @ApiModelProperty(
    //         value = "返回信息",
    //         name = "msg"
    // )
    /** 返回信息 */
    public static final String MSG_TAG = "msg";

    // @ApiModelProperty(
    //         value = "数据对象",
    //         name = "data"
    // )
    /** 数据对象 */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 ResultData 对象，使其表示一个空消息。
     */
    public ResultData()
    {
    }

    /**
     * 初始化一个新创建的 ResultData 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public ResultData(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 ResultData 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public ResultData(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (!Objects.isNull(data))
        {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static ResultData success()
    {
        return success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static ResultData success(Object data)
    {
        return success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static ResultData success(String msg)
    {
        return success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static ResultData success(String msg, Object data)
    {
        return success(HttpStatus.SUCCESS, msg, data);
    }
    /**
     * 返回成功消息
     *
     * @param code 响应码
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static ResultData success(int code, String msg, Object data)
    {
        return new ResultData(code, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static ResultData error()
    {
        return ResultData.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static ResultData error(String msg)
    {
        return ResultData.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static ResultData error(String msg, Object data)
    {
        return new ResultData(HttpStatus.ERROR, msg, data);
    }
    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static ResultData error(int code, String msg, Object data)
    {
        return new ResultData(code, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static ResultData error(int code, String msg)
    {
        return new ResultData(code, msg, null);
    }

    /**
     * 枚举创建错误
     *
     * @return
     */
    public static ResultData error(WebErrorCodeEnum codeEnum)
    {
        return byEnum(codeEnum, null);
    }

    /**
     * 枚举创建错误
     *
     * @return
     */
    public static ResultData error(WebErrorCodeEnum codeEnum, Object data)
    {
        return byEnum(codeEnum, data);
    }

    /**
     * 枚举创建
     *
     * @return
     */
    public static ResultData byEnum(WebErrorCodeEnum codeEnum, Object data)
    {
        return new ResultData(codeEnum.getCode(), codeEnum.getMessage(), data);
    }

    /**
     * 方便链式调用
     *
     * @param key 键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public ResultData put(String key, Object value)
    {
        super.put(key, value);
        return this;
    }

    public void setCode(int code) {
        put(CODE_TAG, code);
    }

    public int getCode() {
        return (int) get(CODE_TAG);
    }

    public void setMsg(String msg) {
        put(MSG_TAG, msg);
    }

    public String getMsg() {
        return (String) get(MSG_TAG);
    }

    public void setData(T data) {
        put(DATA_TAG, data);
    }

    public Object getData() {
        Object obj = get(DATA_TAG);
        return obj;
    }

}
