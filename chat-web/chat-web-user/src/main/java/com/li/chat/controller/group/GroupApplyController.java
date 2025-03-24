package com.li.chat.controller.group;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.enums.GroupApplyEnum;
import com.li.chat.common.enums.GroupJoinModeEnum;
import com.li.chat.common.enums.GroupMemberTypeEnum;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.feign.GroupApplyFeign;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.param.group.GroupApplyParam;
import com.li.chat.param.group.GroupInviteParam;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author malaka
 */

@Api(tags = "0202群组申请接口")
@RestController
@RequestMapping("/group/apply")
public class GroupApplyController {

    private final GroupApplyFeign groupApplyFeign;

    private final GroupManagementFeign groupManagementFeign;

    private final GroupMemberFeign groupMemberFeign;

    private final UserFeign userFeign;

    public GroupApplyController(GroupApplyFeign groupApplyFeign,
                                GroupManagementFeign groupManagementFeign,
                                GroupMemberFeign groupMemberFeign,
                                UserFeign userFeign) {
        this.groupApplyFeign = groupApplyFeign;
        this.groupManagementFeign = groupManagementFeign;
        this.groupMemberFeign = groupMemberFeign;
        this.userFeign = userFeign;
    }

    @ApiOperation(value = "申请加入群聊")
    @GlobalTransactional
    @PostMapping("/apply")
    public ResultData apply(@RequestBody @Valid  GroupApplyParam param) {
        Long userId = Long.valueOf(RequestContext.getUserId());
        Long groupId = param.getGroupId();

        GroupDTO groupDTO = groupManagementFeign.findGroupById(groupId);
        // 群组不存在
        if (BeanUtil.isEmpty(groupDTO)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_NOT_FOUND);
        }
        // 私密群组
        if (GroupJoinModeEnum.MODE_PRIVATE.equals(groupDTO.getJoinMode())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_PRIVATE_GROUP);
        }
        // 已是群成员
        boolean isMember = groupMemberFeign.isGroupMember(userId, groupId);
        if (isMember) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_ALREADY_A_GROUP_MEMBER);
        }
        GroupApplyDTO groupApplyDTO = GroupApplyDTO.builder()
                .userId(userId)
                .groupId(groupId)
                .type(GroupApplyEnum.TYPE_APPLY)
                .status(GroupApplyEnum.STATUS_UNTREATED)
                .message(param.getMessage())
                .build();
        Long id = groupApplyFeign.createApply(groupApplyDTO);
        return ResultData.success().put("id", id);
    }

    @ApiOperation(value = "邀请加入群聊")
    @GlobalTransactional
    @PostMapping("/invite")
    public ResultData invite(@RequestBody @Valid  GroupInviteParam param) {
        Long userId = RequestContext.getUserId();
        Long groupId = param.getGroupId();
        System.out.println("param.getUserIdList() = " + param.getUserIdList());
        GroupDTO groupDTO = groupManagementFeign.findGroupById(groupId);
        // 群组不存在
        if (BeanUtil.isEmpty(groupDTO)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_NOT_FOUND);
        }
        GroupMemberDTO member = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        // 不是群成员不能邀请其他用户
        if (member == null) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_ALREADY_NOT_A_GROUP_MEMBER);
        }
        // 移除不存在的用户
        List<Long> userIds = userFeign.filterNotExistIds(param.getUserIdList());
        if (userIds.isEmpty()) {
            return ResultData.success();
        }
        Integer type = member.getType();
        // 管理或群主直接拉入
        if (GroupMemberTypeEnum.TYPE_MANAGER.equals(type)
                || GroupMemberTypeEnum.TYPE_MASTER.equals(type)) {
            for (Long id : userIds) {
                GroupMemberDTO newMember = GroupMemberDTO.builder()
                        .userId(id)
                        .groupId(groupId)
                        .type(GroupMemberTypeEnum.TYPE_MEMBER)
                        .build();
                groupMemberFeign.create(newMember);
            }
            return ResultData.success(userIds);
        }
        List<GroupApplyDTO> inviteList = new ArrayList<>();
        for (Long id : userIds) {
            GroupApplyDTO groupApplyDTO = GroupApplyDTO.builder()
                    .userId(id)
                    .groupId(groupId)
                    .type(GroupApplyEnum.TYPE_INVITE)
                    .status(GroupApplyEnum.STATUS_UNTREATED)
                    .inviteUserId(userId)
                    .build();
            inviteList.add(groupApplyDTO);
        }
        groupApplyFeign.createInvites(inviteList);
        return ResultData.success(userIds);
    }

    @ApiOperation(value = "同意申请")
    @GlobalTransactional
    @PutMapping("/agreeApply")
    public ResultData agreeApply(@RequestParam("applyId") Long applyId) {
        GroupApplyDTO applyDTO = groupApplyFeign.findById(applyId);
        if (BeanUtil.isEmpty(applyDTO)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NO_FOUND);
        }
        Long userId = RequestContext.getUserId();
        Long groupId = applyDTO.getGroupId();
        GroupMemberDTO groupMemberDTO = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        if (groupMemberDTO==null) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NOT_MANAGER);
        }
        Integer type = groupMemberDTO.getType();
        // 管理员或群主可同意
        if (Objects.equals(type, GroupMemberTypeEnum.TYPE_MANAGER) || Objects.equals(type, GroupMemberTypeEnum.TYPE_MASTER)) {
            GroupMemberDTO member = GroupMemberDTO.builder()
                    .userId(applyDTO.getUserId())
                    .groupId(groupId)
                    .type(GroupMemberTypeEnum.TYPE_MEMBER)
                    .build();
            groupMemberFeign.create(member);
            groupApplyFeign.agreeApply(applyDTO);
        }else {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NOT_MANAGER);
        }
        return ResultData.success();
    }

    @ApiOperation(value = "用户管理的群组申请列表")
    @GetMapping("/myGroupApply")
    public ResultData myGroupApply() {
        Long userId = RequestContext.getUserId();
        List<GroupApplyDTO> applyDTOList = groupApplyFeign.findGroupApplyByUserId(userId);
        return ResultData.success(applyDTOList);
    }

}
