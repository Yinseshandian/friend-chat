package com.li.chat.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author malaka
 */
@Getter
public enum TalkTypeEnum {

    /**
     * 单聊
     */
    SINGLE("SINGLE", "单聊"),
    /**
     * 群聊
     */
    GROUP("GROUP", "群聊"),
    ;

    @JsonValue
    private String code;

    private String info;

    TalkTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
