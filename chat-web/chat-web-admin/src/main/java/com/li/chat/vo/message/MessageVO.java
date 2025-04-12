package com.li.chat.vo.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author malaka
 */
@ApiModel(value = "聊天消息列表VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {
    @ApiModelProperty(value = "消息ID", example = "1")
    private String id;

    @ApiModelProperty(value = "发送人ID", example = "10001")
    private String fromId;

    @ApiModelProperty(value = "发送人用户名", example = "zhangsan")
    private String fromUsername;

    @ApiModelProperty(value = "发送人头像", example = "avatar.jpg")
    private String fromAvatar;

    @ApiModelProperty(value = "接收人/群ID", example = "10002")
    private String toId;

    @ApiModelProperty(value = "接收人/群名称", example = "lisi")
    private String toName;

    @ApiModelProperty(value = "接收人/群头像", example = "avatar.jpg")
    private String toAvatar;

    @ApiModelProperty(value = "消息类型", example = "TEXT")
    private String msgType;

    @ApiModelProperty(value = "聊天类型", example = "SINGLE")
    private String talkType;

    @ApiModelProperty(value = "消息内容预览", example = "Hello, world...")
    private String contentPreview;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}