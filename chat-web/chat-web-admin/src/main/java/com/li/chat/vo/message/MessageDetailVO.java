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
@ApiModel(value = "聊天消息详情VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDetailVO {
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

    @ApiModelProperty(value = "接收方类型", example = "用户")
    private String toType;

    @ApiModelProperty(value = "消息类型", example = "TEXT")
    private String msgType;

    @ApiModelProperty(value = "聊天类型", example = "SINGLE")
    private String talkType;

    @ApiModelProperty(value = "原始消息内容", example = "Hello, world")
    private String content;

    @ApiModelProperty(value = "文本内容", example = "Hello, world")
    private String textContent;

    @ApiModelProperty(value = "媒体名称", example = "image.jpg")
    private String mediaName;

    @ApiModelProperty(value = "媒体URL", example = "http://example.com/image.jpg")
    private String mediaUrl;

    @ApiModelProperty(value = "视频URL", example = "http://example.com/video.mp4")
    private String videoUrl;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}