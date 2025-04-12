package com.li.chat.controller.message;

import com.alibaba.fastjson.JSONObject;
import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.enums.PushMsgTypeEnum;
import com.li.chat.common.enums.TalkTypeEnum;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.domain.DTO.message.MessageDTO;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.message.feign.MessageFeign;
import com.li.chat.vo.message.MessageDetailVO;
import com.li.chat.vo.message.MessageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@Api(tags = "聊天消息管理接口")
@RestController
@RequestMapping("/chat/message")
public class MessageController {

    private final MessageFeign messageFeign;
    private final UserFeign userFeign;
    private final GroupManagementFeign groupManagementFeign;

    public MessageController(MessageFeign messageFeign, UserFeign userFeign,
                             GroupManagementFeign groupManagementFeign) {
        this.messageFeign = messageFeign;
        this.userFeign = userFeign;
        this.groupManagementFeign = groupManagementFeign;
    }

    @ApiOperation("查询消息列表")
    @GetMapping("/search")
    @RequiresPermission(value = "chat:message:list", message = "没有查询消息列表的权限")
    public ResultData search(
            MessageDTO messageDTO,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 调用微服务查询消息
        PageResultData<MessageDTO> pageData = messageFeign.search(messageDTO, pageNum, pageSize);

        // 收集用户ID和群组ID
        Set<Long> userIds = new HashSet<>();
        Set<Long> groupIds = new HashSet<>();

        for (MessageDTO message : pageData.getRows()) {
            if (message.getFromId() != null) {
                userIds.add(message.getFromId());
            }

            if (TalkTypeEnum.SINGLE.getCode().equals(message.getTalkType()) && message.getToId() != null) {
                userIds.add(message.getToId());
            } else if (TalkTypeEnum.GROUP.getCode().equals(message.getTalkType()) && message.getToId() != null) {
                groupIds.add(message.getToId());
            }
        }

        // 批量查询用户和群组信息
        Map<Long, UserDTO> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserDTO> users = userFeign.findAllUnDelByIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, user -> user, (k1, k2) -> k1));
        }

        Map<Long, GroupDTO> groupMap = new HashMap<>();
        for (Long groupId : groupIds) {
            GroupDTO group = groupManagementFeign.findGroupById(groupId);
            if (group != null) {
                groupMap.put(groupId, group);
            }
        }

        // 组装结果
        List<MessageVO> voList = new ArrayList<>();
        for (MessageDTO message : pageData.getRows()) {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(message, vo);
            vo.setId(message.getId() + "");
            vo.setFromId(message.getFromId() + "");
            vo.setToId(message.getToId() + "");
            // 设置发送方信息
            if (message.getFromId() != null) {
                UserDTO fromUser = userMap.get(message.getFromId());
                if (fromUser != null) {
                    vo.setFromUsername(fromUser.getUsername());
                    vo.setFromAvatar(fromUser.getAvatar());
                }
            }

            // 设置接收方信息
            if (message.getToId() != null) {
                if (TalkTypeEnum.SINGLE.getCode().equals(message.getTalkType())) {
                    UserDTO toUser = userMap.get(message.getToId());
                    if (toUser != null) {
                        vo.setToName(toUser.getUsername());
                        vo.setToAvatar(toUser.getAvatar());
                    }
                } else if (TalkTypeEnum.GROUP.getCode().equals(message.getTalkType())) {
                    GroupDTO toGroup = groupMap.get(message.getToId());
                    if (toGroup != null) {
                        vo.setToName(toGroup.getName());
                        vo.setToAvatar(toGroup.getPhoto());
                    }
                }
            }

            // 处理消息内容预览
            vo.setContentPreview(formatContentPreview(message.getContent(), message.getMsgType()));

            voList.add(vo);
        }

        // 构建最终结果
        PageResultData<MessageVO> result = new PageResultData<>();
        result.setTotal(pageData.getTotal());
        result.setRows(voList);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return ResultData.success(result);
    }

    @ApiOperation("获取消息详情")
    @GetMapping("/detail/{id}")
    @RequiresPermission(value = "chat:message:info", message = "没有查看消息详情的权限")
    public ResultData getDetail(@PathVariable("id") Long id) {
        // 查询消息
        MessageDTO message = messageFeign.getDetail(id);

        if (message == null) {
            return ResultData.error("消息不存在");
        }

        // 创建详情VO
        MessageDetailVO detailVO = new MessageDetailVO();
        BeanUtils.copyProperties(message, detailVO);
        detailVO.setId(message.getId() + "");
        detailVO.setFromId(message.getFromId() + "");
        detailVO.setToId(message.getToId() + "");
        // 查询发送方信息
        if (message.getFromId() != null) {
            UserDTO fromUser = userFeign.findUserById(message.getFromId());
            if (fromUser != null) {
                detailVO.setFromUsername(fromUser.getUsername());
                detailVO.setFromAvatar(fromUser.getAvatar());
            }
        }

        // 查询接收方信息
        if (message.getToId() != null) {
            if (TalkTypeEnum.SINGLE.getCode().equals(message.getTalkType())) {
                UserDTO toUser = userFeign.findUserById(message.getToId());
                if (toUser != null) {
                    detailVO.setToName(toUser.getUsername());
                    detailVO.setToAvatar(toUser.getAvatar());
                    detailVO.setToType("用户");
                }
            } else if (TalkTypeEnum.GROUP.getCode().equals(message.getTalkType())) {
                GroupDTO toGroup = groupManagementFeign.findGroupById(message.getToId());
                if (toGroup != null) {
                    detailVO.setToName(toGroup.getName());
                    detailVO.setToAvatar(toGroup.getPhoto());
                    detailVO.setToType("群组");
                }
            }
        }

        // 解析消息内容
        parseMessageContent(detailVO);

        return ResultData.success(detailVO);
    }

    /**
     * 格式化消息内容预览
     */
    private String formatContentPreview(String content, String msgType) {
        if (StringUtils.isBlank(content)) {
            return "[空消息]";
        }

        if (PushMsgTypeEnum.TEXT.getCode().equals(msgType)) {
            // 文本消息，直接截取
            return content.length() > 20 ? content.substring(0, 20) + "..." : content;
        } else if (PushMsgTypeEnum.IMAGE.getCode().equals(msgType)) {
            return "[图片消息]";
        } else if (PushMsgTypeEnum.VOICE.getCode().equals(msgType)) {
            return "[语音消息]";
        } else if (PushMsgTypeEnum.VIDEO.getCode().equals(msgType)) {
            return "[视频消息]";
        } else if (PushMsgTypeEnum.ALERT.getCode().equals(msgType)) {
            return "[通知消息]";
        } else {
            return "[未知类型消息]";
        }
    }

    /**
     * 解析消息内容
     */
    private void parseMessageContent(MessageDetailVO detailVO) {
        String content = detailVO.getContent();
        String msgType = detailVO.getMsgType();

        if (StringUtils.isBlank(content)) {
            return;
        }

        if (PushMsgTypeEnum.TEXT.getCode().equals(msgType)) {
            // 文本消息，直接设置
            detailVO.setTextContent(content);

        } else if (PushMsgTypeEnum.IMAGE.getCode().equals(msgType)
                || PushMsgTypeEnum.VOICE.getCode().equals(msgType)
                || PushMsgTypeEnum.VIDEO.getCode().equals(msgType)) {

            try {
                // 解析JSON内容
                JSONObject jsonObject = JSONObject.parseObject(content);

                detailVO.setMediaName(jsonObject.getString("name"));
                detailVO.setMediaUrl(jsonObject.getString("url"));

                // 视频消息特殊处理
                if (PushMsgTypeEnum.VIDEO.getCode().equals(msgType)) {
                    detailVO.setVideoUrl(jsonObject.getString("videoUrl"));
                }

            } catch (Exception e) {
                detailVO.setTextContent("[无法解析的多媒体消息]");
            }
        }
    }
}