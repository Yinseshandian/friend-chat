package com.li.chat.controller.group;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.enums.GroupApplyEnum;
import com.li.chat.common.enums.GroupJoinModeEnum;
import com.li.chat.common.enums.GroupMemberTypeEnum;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.feign.GroupApplyFeign;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.param.group.GroupApplyParam;
import com.li.chat.param.group.GroupInviteParam;
import com.li.chat.vo.ApplyVo;
import com.li.chat.vo.GroupApplyVo;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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
        Long userId = RequestContext.getUserId();
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
    @PutMapping("/agree/{applyId}")
    public ResultData agreeApply(@PathVariable("applyId") Long applyId) {
        // 查找申请
        GroupApplyDTO applyDTO = groupApplyFeign.findById(applyId);
        if (BeanUtil.isEmpty(applyDTO)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NO_FOUND);
        }
        // 查找当前用户
        Long userId = RequestContext.getUserId();
        Long groupId = applyDTO.getGroupId();
        GroupMemberDTO groupMemberDTO = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        if (groupMemberDTO==null) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NOT_MANAGER);
        }
        Integer type = groupMemberDTO.getType();
        // 当前用户是管理员或群主可同意
        if (!(Objects.equals(type, GroupMemberTypeEnum.TYPE_MANAGER) || Objects.equals(type, GroupMemberTypeEnum.TYPE_MASTER))) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NOT_MANAGER);
        }
        // 已是群成员
        if (groupMemberFeign.isGroupMember(applyDTO.getUserId(), groupId)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_ALREADY_A_GROUP_MEMBER);
        }
        GroupMemberDTO member = GroupMemberDTO.builder()
                .userId(applyDTO.getUserId())
                .groupId(groupId)
                .type(GroupMemberTypeEnum.TYPE_MEMBER)
                .build();
        applyDTO.setStatus(GroupApplyEnum.STATUS_AGREE);
        groupMemberFeign.create(member);
        applyDTO.setProcessedBy(userId);
        groupApplyFeign.update(applyDTO);
        return ResultData.success();
    }

    @ApiOperation(value = "拒绝申请")
    @GlobalTransactional
    @PutMapping("/refuse/{applyId}")
    public ResultData refuse(@PathVariable("applyId") Long applyId) {
        GroupApplyDTO applyDTO = groupApplyFeign.findById(applyId);
        if (BeanUtil.isEmpty(applyDTO) && GroupApplyEnum.STATUS_REFUSE.equals(applyDTO.getStatus())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NO_FOUND);
        }
        Long userId = RequestContext.getUserId();
        Long groupId = applyDTO.getGroupId();

        GroupMemberDTO groupMemberDTO = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        if (groupMemberDTO==null) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NOT_MANAGER);
        }
        Integer type = groupMemberDTO.getType();
        // 管理员或群主
        if (!(Objects.equals(type, GroupMemberTypeEnum.TYPE_MANAGER) || Objects.equals(type, GroupMemberTypeEnum.TYPE_MASTER))) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NOT_MANAGER);
        }
        applyDTO.setStatus(GroupApplyEnum.STATUS_REFUSE);
        groupApplyFeign.update(applyDTO);

        return ResultData.success();
    }

    @ApiOperation(value = "用户管理的群组申请列表")
    @GetMapping("/manageApply")
    public PageResultData manageApply(PageParam param) {
        Long userId = RequestContext.getUserId();
        PageResultData<GroupApplyDTO> applyPage = groupApplyFeign.findGroupApplyByUserId(userId, param);
        Collection<GroupApplyDTO> rows = applyPage.getRows();
        Set<Long> userIdSet = new HashSet<>();
        rows.forEach(v -> {
            userIdSet.add(v.getUserId());
            userIdSet.add(v.getInviteUserId());
        });
        Set<Long> groupIdSet = rows.stream().map(GroupApplyDTO::getGroupId).collect(Collectors.toSet());

        Map<Long, UserDTO> userIdMap = userFeign.findAllUnDelByIds(userIdSet)
                .stream().collect(Collectors.toMap(UserDTO::getId, v -> v));
        Map<Long, GroupDTO> groupIdMap = groupManagementFeign.findGroupByIds(groupIdSet)
                .stream().collect(Collectors.toMap(GroupDTO::getId, v -> v));

        List<GroupApplyVo> applyVoList = rows.stream().map(v -> {
            UserDTO userDTO = userIdMap.get(v.getUserId());
            GroupDTO groupDTO = groupIdMap.get(v.getGroupId());
            GroupApplyVo applyVo = GroupApplyVo.builder()
                    .avatar(userDTO.getAvatar())
                    .nickname(userDTO.getNickname())
                    .username(userDTO.getUsername())
                    .groupName(groupDTO.getName())
                    .sex(userDTO.getSex())
                    .build();
            if (v.getInviteUserId() != null) {
                UserDTO inviteUser = userIdMap.get(v.getInviteUserId());
                applyVo.setInviteUserName(inviteUser.getNickname());
            }
            BeanUtil.copyProperties(v, applyVo);
            return applyVo;
        }).collect(Collectors.toList());
        PageResultData<GroupApplyVo> resultData = PageResultData.<GroupApplyVo>builder()
                .pageNum(param.getPageNum())
                .pageSize(param.getPageSize())
                .total(applyPage.getTotal())
                .rows(applyVoList)
                .build();
        resultData.setCode(HttpStatus.OK.value());
        return resultData;
    }

    @ApiOperation(value = "详情")
    @GetMapping("/info/{id}")
    public ResultData manageApply(@PathVariable("id") Long id) {
        Long userId = RequestContext.getUserId();
        GroupApplyDTO applyDTO = groupApplyFeign.findById(id);
        if (applyDTO == null) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NO_FOUND);
        }
        GroupMemberDTO memberDTO = groupMemberFeign.findByGroupIdAndUserId(applyDTO.getGroupId(), userId);
        if (GroupMemberTypeEnum.TYPE_MEMBER.equals(memberDTO.getType())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_NOT_MANAGER);
        }

        GroupDTO groupDTO = groupManagementFeign.findGroupById(applyDTO.getGroupId());
        UserDTO userDTO = userFeign.findUserById(applyDTO.getUserId());

        GroupApplyVo applyVo = GroupApplyVo.builder()
                .avatar(userDTO.getAvatar())
                .nickname(userDTO.getNickname())
                .username(userDTO.getUsername())
                .groupName(groupDTO.getName())
                .sex(userDTO.getSex())
                .build();
        if (applyDTO.getInviteUserId() != null) {
            userDTO = userFeign.findUserById(applyDTO.getInviteUserId());
            applyVo.setInviteUserName(userDTO.getNickname());
        }
        BeanUtil.copyProperties(applyDTO, applyVo);
        return ResultData.success(applyVo);
    }


    @ApiOperation(value = "加入开放群聊")
    @GlobalTransactional
    @PostMapping("/join/{groupId}")
    public ResultData join(@PathVariable("groupId") Long groupId) {
        Long userId = RequestContext.getUserId();

        GroupDTO groupDTO = groupManagementFeign.findGroupById(groupId);
        // 群组不存在
        if (BeanUtil.isEmpty(groupDTO)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_NOT_FOUND);
        }
        // 已是群成员
        boolean isMember = groupMemberFeign.isGroupMember(userId, groupId);
        if (isMember) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_ALREADY_A_GROUP_MEMBER);
        }
        // 非开放群组不能直接进入
        if (!GroupJoinModeEnum.MODE_OPEN.equals(groupDTO.getJoinMode())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_APPLY_PRIVATE_GROUP);
        }
        GroupMemberDTO member = GroupMemberDTO.builder()
                .userId(userId)
                .groupId(groupId)
                .type(GroupMemberTypeEnum.TYPE_MEMBER)
                .build();
        return ResultData.success();
    }
}
