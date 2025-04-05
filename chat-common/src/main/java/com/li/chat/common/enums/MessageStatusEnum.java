package com.li.chat.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author malaka
 */

@Getter
public enum MessageStatusEnum {

    /**
     * 正常
     */
    OK("0", "正常"),
    /**
     * 对方不是自己朋友
     */
    FRIEND_TO("1", "对方不是你的好友，消息发送失败"),
    /**
     * 自己不是对方朋友
     */
    FRIEND_FROM("2", "你不是对方的好友，消息发送失败"),
    /**
     * 黑名单
     */
    FRIEND_BLACK("3", "消息已发出，但被对方拒收了"),
    /**
     * 注销
     */
    FRIEND_DELETED("4", "对方已注销，消息发送失败"),
    /**
     * 群不存在
     */
    GROUP_NOT_EXIST("5", "当前群不存在，消息发送失败"),
    /**
     * 群明细不存在
     */
    GROUP_INFO_NOT_EXIST("6", "你不在当前群中，消息发送失败"),

    USER_OFFLINE("7", "你还没未上线，请先登录"),

    UNKNOWN_ERR("-1", "未知错误"),
    ;

    @JsonValue
    private String code;
    private String info;

    MessageStatusEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
