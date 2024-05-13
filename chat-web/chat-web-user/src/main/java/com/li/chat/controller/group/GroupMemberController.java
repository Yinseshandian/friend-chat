package com.li.chat.controller.group;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.li.chat.common.enums.GroupMemberTypeEnum;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.vo.GroupMemberVo;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@Api(tags = "0203群组成员接口")
@RestController
@RequestMapping("/group/member")
public class GroupMemberController {

    private final GroupMemberFeign groupMemberFeign;

    private final UserFeign userFeign;

    public GroupMemberController(GroupMemberFeign groupMemberFeign, UserFeign userFeign) {
        this.groupMemberFeign = groupMemberFeign;
        this.userFeign = userFeign;
    }

    @ApiOperation(value = "获取群成员")
    @GlobalTransactional
    @GetMapping("/list")
    public ResultData create(Long groupId) {
        Long userId = RequestContext.getUserId();
        GroupMemberDTO member = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        if (ObjectUtil.isEmpty(member)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MEMBER);
        }
        List<GroupMemberDTO> groupMemberDTOList = groupMemberFeign.findAllByGroupId(groupId);
        // 通过用户id列表 获取用户信息
        List<Long> userIdList = groupMemberDTOList.stream().mapToLong(GroupMemberDTO::getUserId).boxed().collect(Collectors.toList());
        List<UserDTO> userDTOList = userFeign.findAllUnDelByIds(userIdList);
        // 排序成员列表，保证顺序和用户列表一致
        groupMemberDTOList.sort(Comparator.comparingLong(GroupMemberDTO::getUserId));
        userDTOList.sort(Comparator.comparingLong(UserDTO::getId));

        List<GroupMemberVo> groupMemberVoList = new ArrayList<>();
        for (int i = 0; i < groupMemberDTOList.size(); i++) {
            GroupMemberDTO groupMemberDTO = groupMemberDTOList.get(i);
            UserDTO userDTO = userDTOList.get(i);
            GroupMemberVo groupMemberVo = GroupMemberVo.builder()
                    .id(groupMemberDTO.getId())
                    .userId(groupMemberDTO.getUserId())
                    .type(groupMemberDTO.getType())
                    .remark(groupMemberDTO.getNickname())
                    .username(userDTO.getUsername())
                    .nickname(userDTO.getNickname())
                    .avatar(userDTO.getAvatar())
                    .build();
            groupMemberVoList.add(groupMemberVo);
        }

        return ResultData.success(groupMemberVoList);
    }

    @ApiOperation(value = "退出群聊")
    @GlobalTransactional
    @DeleteMapping("/quit")
    public ResultData quit(Long groupId) {
        Long userId = RequestContext.getUserId();
        GroupMemberDTO groupMemberDTO = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        if (BeanUtil.isEmpty(groupMemberDTO)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MEMBER);
        }
        if (ObjectUtil.equal(groupMemberDTO.getType(), GroupMemberTypeEnum.TYPE_MASTER)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_MASTER_CAN_NOT_QUIT);
        }
        int num = groupMemberFeign.deleteById(groupMemberDTO.getId());
        if (num == 0) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_QUIT_FAIL);
        }
        return ResultData.success();
    }


    @ApiOperation(value = "踢出群聊")
    @GlobalTransactional
    @DeleteMapping("/kick")
    public ResultData kick(Long groupId, Long memberId) {
        Long userId = RequestContext.getUserId();
        GroupMemberDTO manager = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        if (BeanUtil.isEmpty(manager)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MANAGER);
        }
        Integer managerType = manager.getType();
        if (ObjectUtil.notEqual(managerType, GroupMemberTypeEnum.TYPE_MASTER)
                && ObjectUtil.notEqual(managerType, GroupMemberTypeEnum.TYPE_MANAGER)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MANAGER);
        }
        GroupMemberDTO member = groupMemberFeign.findById(memberId);
        if (BeanUtil.isEmpty(member) || ObjectUtil.notEqual(member.getGroupId(), manager.getGroupId())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MEMBER);
        }
        if (manager.getType() <= member.getType()) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_DEL_NOT_PERMISSIONS);
        }
        groupMemberFeign.deleteById(memberId);
        return ResultData.success();
    }


    @ApiOperation(value = "设置管理员")
    @GlobalTransactional
    @PutMapping("/setManager")
    public ResultData setManager(Long groupId, Long memberId) {
        Long userId = RequestContext.getUserId();
        GroupMemberDTO manager = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);

        if (BeanUtil.isEmpty(manager)
           || ObjectUtil.notEqual(manager.getType(), GroupMemberTypeEnum.TYPE_MASTER)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MASTER);
        }
        GroupMemberDTO member = groupMemberFeign.findById(memberId);
        if (BeanUtil.isEmpty(member) || ObjectUtil.notEqual(member.getGroupId(), manager.getGroupId())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MEMBER);
        }

        if (ObjectUtil.equal(manager.getId(), member.getId())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_MASTER_CANT_BE_MANAGER);
        }
        member.setType(GroupMemberTypeEnum.TYPE_MANAGER);
        groupMemberFeign.update(member);
        return ResultData.success();
    }

    @ApiOperation(value = "移除管理员")
    @GlobalTransactional
    @PutMapping("/removeManager")
    public ResultData removeManager(Long groupId, Long memberId) {
        Long userId = RequestContext.getUserId();
        GroupMemberDTO manager = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);

        if (BeanUtil.isEmpty(manager)
                || ObjectUtil.notEqual(manager.getType(), GroupMemberTypeEnum.TYPE_MASTER)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MASTER);
        }

        GroupMemberDTO member = groupMemberFeign.findById(memberId);
        if (BeanUtil.isEmpty(manager) || ObjectUtil.notEqual(member.getGroupId(), manager.getGroupId())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MEMBER);
        }
        if (ObjectUtil.equal(manager.getId(), member.getId())) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_MANAGER_CANT_REMOVE);
        }
        member.setType(GroupMemberTypeEnum.TYPE_MEMBER);
        groupMemberFeign.update(member);
        return ResultData.success();
    }



}
