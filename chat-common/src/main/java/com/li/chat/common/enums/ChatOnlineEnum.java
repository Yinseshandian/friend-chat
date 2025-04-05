package com.li.chat.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;

/**
 * @author malaka
 */
@Getter
public enum ChatOnlineEnum {

    ONLINE_CURRENT("1", "当前服务器在线"),

    ONLINE_OTHER("2", "其他服务器在线"),

    OFFLINE("0","离线")
    ;
    @JsonValue
    private String code;

    private String onlineStatus;

    ChatOnlineEnum(String code, String onlineStatus) {
        this.code = code;
        this.onlineStatus = onlineStatus;
    }
}
