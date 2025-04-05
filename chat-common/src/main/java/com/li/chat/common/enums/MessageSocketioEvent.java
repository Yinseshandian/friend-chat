package com.li.chat.common.enums;

/**
 * @author malaka
 * socketio消息事件常量
 */
public class MessageSocketioEvent {

    public static final String EVENT_ONLINE = "online";
    public static final String EVENT_SEND_MESSAGE = "send_message";
    public static final String EVENT_MESSAGE_ACK = "message_ack";
    public static final String EVENT_PULL_OFFLINE_MESSAGE = "pull_offline_message";
    public static final String SEND_OFFLINE_MESSAGE = "offline_message";
    public static final String EVENT_SEND_GROUP_MESSAGE = "send_group_message";
    public static final String SEND_NEW_MESSAGE = "new_message";
    public static final String EVENT_LOGOUT = "logout";

}
