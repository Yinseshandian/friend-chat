package com.li.chat.common.enums;

/**
 * 错误枚举
 * @author malaka
 */
public enum WebErrorCodeEnum {

    /**
     * 服务器错误
     */
    SERVICE_ERROR(500, "连接失败，请重试"),

    /**
     * 0101用户认证相关接口
     */
    USER_AUTH_WRONG_CAPTCHA(50101001, "验证码错误"),
    USER_AUTH_USER_ALREADY_EXISTS(50101002, "账号已被注册"),
    USER_AUTH_WRONG_INPUT_USER_OR_PASSWORD(50101003, "用户名或密码输入错误"),
    USER_AUTH_LOGIN_FAIL(50101004, "登录失败"),

    /**
     * 0102用户信息相关接口
     */
    USER_INFO_OLD_PASSWORD_WRONG(50102001, "旧密码错误"),
    USER_INFO_AVATAR_FILE_TYPE_WRONG(50102002, "文件类型错误"),
    USER_INFO_AVATAR_FILE_SIZE_WRONG(50102003, "头像尺寸为100x100"),
    USER_INFO_USER_NOT_FOUND(50102004, "用户不存在"),

    /**
     * 0103好友相关接口
     */
    USER_FRIEND_ALREADY_IS_FRIEND(50103001, "已为好友，请勿重复添加"),
    USER_FRIEND_APPLY_NO_FOUND(50103002, "未找到好友申请"),
    USER_FRIEND_APPLY_FINISH(50103003, "已处理的好友申请"),
    USER_FRIEND_NO_FRIEND(50103004, "好友不存在"),
    USER_FRIEND_DEL_FAIL(50103005, "删除失败，请重试"),
    USER_FRIEND_USER_NOT_FOUND(50103006, "用户不存在"),


    /**
     * 0201群组管理接口
     */

    /**
     * 0202群组申请接口
     */
    GROUP_NOT_FOUND(50202001, "群组不存在"),
    GROUP_APPLY_PRIVATE_GROUP(50202002, "私密群组"),
    GROUP_APPLY_ALREADY_A_GROUP_MEMBER(50202003, "当前用户已是群成员"),
    GROUP_APPLY_ALREADY_NOT_A_GROUP_MEMBER(50202004, "当前用户不是群成员"),
    GROUP_APPLY_NO_FOUND(50202005, "群申请不存在"),
    GROUP_APPLY_NOT_MANAGER(50202005, "不是该群管理员"),
    GROUP_APPLY_NOT_OPEN(50202006, "不是开放群聊"),
    /**
     * 0203群组成员接口
     */
    GROUP_MEMBER_NOT_MEMBER(50203001, "不是群成员"),
    GROUP_MEMBER_MASTER_CAN_NOT_QUIT(50203002, "群主无法退出"),
    GROUP_MEMBER_NOT_MANAGER(50203003, "不是管理员"),
    GROUP_MEMBER_DEL_NOT_PERMISSIONS(50203004, "无权限删除"),
    GROUP_MEMBER_NOT_MASTER(50203005, "不是群主"),
    GROUP_MEMBER_MASTER_CANT_BE_MANAGER(50203006, "无法设置自己为管理员"),
    GROUP_MEMBER_MANAGER_CANT_REMOVE(50203007, "无法移除管理员"),
    GROUP_MEMBER_QUIT_FAIL(50203008, "退出失败"),



    ;

    WebErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
