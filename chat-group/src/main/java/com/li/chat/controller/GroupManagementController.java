package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.entity.Group;
import com.li.chat.service.GroupApplyService;
import com.li.chat.service.GroupManagementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
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

    @PutMapping("/update")
    public void update(@RequestBody GroupDTO groupDTO) {
        if (ObjectUtils.isEmpty(groupDTO.getId())) {
            throw new RuntimeException("更新失败，未找到该用户。");
        }
        Group group = new Group();
        BeanUtils.copyProperties(groupDTO, group);
        groupManagementService.update(group);
    }

    @GetMapping("/findByName")
    public PageResultData<GroupDTO> findByName(@RequestParam("name") String name, @SpringQueryMap PageParam pageParam) {
        int pageNum = pageParam.getPageNum();
        int pageSize = pageParam.getPageSize();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Group> applyPage = groupManagementService.findAllOpenByNameLike(name, pageable);
        List<GroupDTO> groupDTOList = new ArrayList<>();
        applyPage.forEach(v -> {
            GroupDTO groupDTO = new GroupDTO();
            BeanUtils.copyProperties(v, groupDTO);
            groupDTOList.add(groupDTO);

        });
        return PageResultData.<GroupDTO>builder()
                .total(applyPage.getTotalElements())
                .rows(groupDTOList)
                .pageSize(pageSize)
                .pageNum(pageNum).build();
    }

    @GetMapping("/search")
    PageResultData<GroupDTO> search(@SpringQueryMap GroupDTO groupDTO,
                                    @RequestParam("pageNum") int pageNum,
                                    @RequestParam("pageSize") int pageSize){
        PageParam pageParam = PageParam.builder().pageNum(pageNum).pageSize(pageSize).build();
        Page<Group> page = groupManagementService.findByGroupDTO(groupDTO, pageParam);
        List<GroupDTO> list = page.stream().map(v -> {
            GroupDTO dto = GroupDTO.builder().build();
            BeanUtil.copyProperties(v, dto);
            return dto;
        }).collect(Collectors.toList());

        return PageResultData.<GroupDTO>builder()
                .total(page.getTotalElements())
                .rows(list)
                .pageSize(pageSize)
                .pageNum(pageNum).build();
    }

    /**
     * id 列表查找群组
     * @param groupIds
     * @return
     */
    @GetMapping("/findGroupByIds")
    public List<GroupDTO> findGroupByIds(@RequestParam("groupIds") Collection<Long> groupIds) {
        List<Group> groupList = groupManagementService.findGroupByIds(groupIds);
        List<GroupDTO> list = groupList.stream().map(v -> {
            GroupDTO dto = GroupDTO.builder().build();
            BeanUtil.copyProperties(v, dto);
            return dto;
        }).collect(Collectors.toList());
        return list;
    }

}
