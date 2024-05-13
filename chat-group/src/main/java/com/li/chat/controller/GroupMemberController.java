package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.entity.Group;
import com.li.chat.entity.GroupApply;
import com.li.chat.entity.GroupMember;
import com.li.chat.service.GroupManagementService;
import com.li.chat.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@RestController
@RequestMapping("/chat-group/member")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    private final GroupManagementService groupManagementService;

    public GroupMemberController(GroupMemberService groupMemberService, GroupManagementService groupManagementService) {
        this.groupMemberService = groupMemberService;
        this.groupManagementService = groupManagementService;
    }

    /**
     * 是否为群成员
     * @param userId
     * @param groupId
     * @return
     */
    @GetMapping("/isGroupMember")
    public boolean isGroupMember(@RequestParam("userId") Long userId,
                          @RequestParam("groupId") Long groupId) {
        boolean isGroupMember = groupMemberService.isGroupMember(userId, groupId);
        return isGroupMember;
    }

    /**
     * 创建群成员
     * @param groupMemberDTO
     * @return
     */
    @PostMapping("/create")
    public Long create(@RequestBody GroupMemberDTO groupMemberDTO) {
        GroupMember groupMember = new GroupMember();
        BeanUtil.copyProperties(groupMemberDTO, groupMember);
        Long groupId = groupMemberDTO.getGroupId();
        groupMember.setGroup(Group.builder().id(groupId).build());
        Long num = groupMemberService.create(groupMember);
        groupManagementService.addMemberNum(groupId, 1);
        return num;
    }


    /**
     * 通过用户id和群组id查找
     * @param userId
     * @param groupId
     * @return
     */
    @GetMapping("/findByGroupIdAndUserId")
    public GroupMemberDTO findByGroupIdAndUserId(@RequestParam("groupId") Long groupId,
                                          @RequestParam("userId") Long userId) {
        GroupMember groupMember = groupMemberService.findByGroupIdAndUserId(groupId, userId);
        if (BeanUtil.isEmpty(groupMember)) {
            return null;
        }
        GroupMemberDTO groupMemberDTO = new GroupMemberDTO();
        BeanUtil.copyProperties(groupMember, groupMemberDTO);
        groupMemberDTO.setGroupId(groupMember.getGroup().getId());
        return groupMemberDTO;
    }

    /**
     * 通过群组查找群成员
     * @param groupId
     * @return
     */
    @GetMapping("/findAllByGroupId")
    public List<GroupMemberDTO> findAllByGroupId(@RequestParam("groupId") Long groupId) {
        List<GroupMember> groupMemberList = groupMemberService.findAllByGroupId(groupId);
        return groupMemberList.stream().map(v -> {
            GroupMemberDTO groupMemberDTO = new GroupMemberDTO();
            BeanUtil.copyProperties(v, groupMemberDTO);
            groupMemberDTO.setGroupId(v.getGroup().getId());
            return groupMemberDTO;
        }).collect(Collectors.toList());
    }


    /**
     * 通过id删除
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById")
    public int deleteById(@RequestParam("id") Long id) {
        return groupMemberService.deleteById(id);
    }


    /**
     * 通过id查找
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public GroupMemberDTO findById(@RequestParam("id") Long id) {
        GroupMember groupMember = groupMemberService.findById(id);
        if (BeanUtil.isEmpty(groupMember)) {
            return null;
        }
        GroupMemberDTO groupMemberDTO = new GroupMemberDTO();
        BeanUtil.copyProperties(groupMember, groupMemberDTO);
        groupMemberDTO.setGroupId(groupMember.getGroup().getId());
        return groupMemberDTO;
    }


    /**
     * 更新
     * @param memberDTO
     */
    @PutMapping("/update")
    public void update(@RequestBody GroupMemberDTO memberDTO) {
        GroupMember groupMember = new GroupMember();
        BeanUtil.copyProperties(memberDTO, groupMember);
        groupMember.setGroup(Group.builder().id(memberDTO.getGroupId()).build());
        groupMemberService.update(groupMember);
    }

}
