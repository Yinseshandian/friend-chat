package com.li.chat.controller.group;

import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.feign.GroupApplyFeign;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.vo.group.GroupApplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@Api(tags = "群组申请管理接口")
@RestController
@RequestMapping("/group/apply")
public class GroupApplyController {

    private final GroupApplyFeign groupApplyFeign;
    private final GroupManagementFeign groupManagementFeign;
    private final UserFeign userFeign;

    public GroupApplyController(GroupApplyFeign groupApplyFeign,
                                GroupManagementFeign groupManagementFeign,
                                UserFeign userFeign) {
        this.groupApplyFeign = groupApplyFeign;
        this.groupManagementFeign = groupManagementFeign;
        this.userFeign = userFeign;
    }

    @ApiOperation("分页查询群组申请列表")
    @GetMapping("/list")
    @RequiresPermission(value = "group:apply:list", message = "没有查询群组申请列表的权限")
    public ResultData list(
            GroupApplyDTO groupApplyDTO,
            PageParam pageParam) {


        // 获取申请列表
        PageResultData<GroupApplyDTO> pageData = groupApplyFeign.search(groupApplyDTO, pageParam.getPageNum(), pageParam.getPageSize());

        Collection<GroupApplyDTO> rawList = pageData.getRows();


        // 收集所有需要查询的ID
        Set<Long> userIds = new HashSet<>();
        Set<Long> groupIds = new HashSet<>();

        for (GroupApplyDTO apply : rawList) {
            if (apply.getUserId() != null) {
                userIds.add(apply.getUserId());
            }
            if (apply.getInviteUserId() != null) {
                userIds.add(apply.getInviteUserId());
            }
            if (apply.getProcessedBy() != null) {
                userIds.add(apply.getProcessedBy());
            }
            if (apply.getGroupId() != null) {
                groupIds.add(apply.getGroupId());
            }
        }

        // 批量查询用户信息
        Map<Long, UserDTO> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserDTO> users = userFeign.findAllUnDelByIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(UserDTO::getId, u -> u));
        }

        // 批量查询群组信息
        Map<Long, GroupDTO> groupMap = new HashMap<>();
        for (Long gid : groupIds) {
            GroupDTO group = groupManagementFeign.findGroupById(gid);
            if (group != null) {
                groupMap.put(gid, group);
            }
        }

        // 组装VO
        List<GroupApplyVO> voList = new ArrayList<>();
        for (GroupApplyDTO apply : rawList) {
            GroupApplyVO vo = GroupApplyVO.builder()
                    .id(apply.getId())
                    .groupId(apply.getGroupId())
                    .userId(apply.getUserId())
                    .inviteUserId(apply.getInviteUserId())
                    .processedBy(apply.getProcessedBy())
                    .message(apply.getMessage())
                    .type(apply.getType())
                    .status(apply.getStatus())
                    .createTime(apply.getCreateTime())
                    .build();

            // 设置群组信息
            GroupDTO group = groupMap.get(apply.getGroupId());
            if (group != null) {
                vo.setGroupName(group.getName());
                vo.setGroupPhoto(group.getPhoto());
            }

            // 设置用户信息
            UserDTO user = userMap.get(apply.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setUserAvatar(user.getAvatar());
            }

            // 设置邀请人信息
            UserDTO inviter = userMap.get(apply.getInviteUserId());
            if (inviter != null) {
                vo.setInviteUsername(inviter.getUsername());
                vo.setInviteUserAvatar(inviter.getAvatar());
            }

            // 设置处理人信息
            UserDTO processor = userMap.get(apply.getProcessedBy());
            if (processor != null) {
                vo.setProcessedByUsername(processor.getUsername());
                vo.setProcessedByAvatar(processor.getAvatar());
            }

            voList.add(vo);
        }

        PageResultData<GroupApplyVO> result = new PageResultData<>();
        result.setTotal(pageData.getTotal());
        result.setRows(voList);
        result.setPageNum(pageParam.getPageNum());
        result.setPageSize(pageParam.getPageSize());

        return ResultData.success(result);
    }

    @ApiOperation("获取群组申请详情")
    @GetMapping("/info/{id}")
    @RequiresPermission(value = "group:apply:info", message = "没有查看群组申请详情的权限")
    public ResultData getInfo(@PathVariable("id") Long id) {
        // 获取申请信息
        GroupApplyDTO apply = groupApplyFeign.findById(id);
        if (apply == null) {
            return ResultData.error("申请记录不存在");
        }

        // 构建VO
        GroupApplyVO vo = GroupApplyVO.builder()
                .id(apply.getId())
                .groupId(apply.getGroupId())
                .userId(apply.getUserId())
                .inviteUserId(apply.getInviteUserId())
                .processedBy(apply.getProcessedBy())
                .message(apply.getMessage())
                .type(apply.getType())
                .status(apply.getStatus())
                .createTime(apply.getCreateTime())
                .build();

        // 设置群组信息
        GroupDTO group = groupManagementFeign.findGroupById(apply.getGroupId());
        if (group != null) {
            vo.setGroupName(group.getName());
            vo.setGroupPhoto(group.getPhoto());
        }

        // 设置用户信息
        if (apply.getUserId() != null) {
            UserDTO user = userFeign.findUserById(apply.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setUserAvatar(user.getAvatar());
            }
        }

        // 设置邀请人信息
        if (apply.getInviteUserId() != null) {
            UserDTO inviter = userFeign.findUserById(apply.getInviteUserId());
            if (inviter != null) {
                vo.setInviteUsername(inviter.getUsername());
                vo.setInviteUserAvatar(inviter.getAvatar());
            }
        }

        // 设置处理人信息
        if (apply.getProcessedBy() != null) {
            UserDTO processor = userFeign.findUserById(apply.getProcessedBy());
            if (processor != null) {
                vo.setProcessedByUsername(processor.getUsername());
                vo.setProcessedByAvatar(processor.getAvatar());
            }
        }

        return ResultData.success(vo);
    }

}