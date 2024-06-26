package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.entity.Group;
import com.li.chat.service.GroupApplyService;
import com.li.chat.service.GroupManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@RestController
@RequestMapping("/chat-group/management")
public class GroupManagementController {

    private final GroupManagementService groupManagementService;
    private final GroupApplyService groupApplyService;

    public GroupManagementController(GroupManagementService groupManagementService, GroupApplyService groupApplyService) {
        this.groupManagementService = groupManagementService;
        this.groupApplyService = groupApplyService;
    }

    /**
     * 创建
     * @param groupDTO
     * @return
     */
    @PostMapping("/create")
    public Long create(@RequestBody GroupDTO groupDTO) {
        Group group = new Group();
        BeanUtil.copyProperties(groupDTO, group);
        groupManagementService.create(group);
        return group.getId();
    }

    @GetMapping("/findGroupById")
    public GroupDTO findGroupById(@RequestParam("groupId") Long groupId) {
        Group group = groupManagementService.findGroupById(groupId);
        System.out.println(group);
        if (BeanUtil.isEmpty(group)) {
            return null;
        }
        GroupDTO groupDTO = new GroupDTO();
        BeanUtil.copyProperties(group, groupDTO);
        return groupDTO;
    }


    /**
     * 通过用户id查找群组
     * @param userId
     * @return
     */
    @GetMapping("/findAllGroupByUserId")
    public List<GroupDTO> findAllGroupByUserId(@RequestParam("userId") Long userId) {
        List<Group> groupList = groupManagementService.findAllGroupByUserId(userId);
        return groupList.stream().map(v -> {
            GroupDTO groupDTO = GroupDTO.builder().build();
            BeanUtil.copyProperties(v, groupDTO);
            return groupDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 通过id删除
     * @param id
     */
    @DeleteMapping("/deleteById")
    public int deleteById(@RequestParam("id") Long id) {
        return groupManagementService.deleteById(id);
    }

}
