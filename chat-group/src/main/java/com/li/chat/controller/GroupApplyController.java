package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.entity.Group;
import com.li.chat.entity.GroupApply;
import com.li.chat.service.GroupApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author malaka
 */
@RestController
@RequestMapping("/chat-group/apply")
public class GroupApplyController {

    private final GroupApplyService groupApplyService;

    public GroupApplyController(GroupApplyService groupApplyService) {
        this.groupApplyService = groupApplyService;
    }

    @PostMapping("/createApply")
    public Long createApply(@RequestBody GroupApplyDTO groupApplyDTO) {
        GroupApply groupApply = new GroupApply();
        BeanUtil.copyProperties(groupApplyDTO, groupApply);
        return groupApplyService.createApply(groupApply);
    }

    /**
     * 创建邀请列表
     * @param inviteList
     */

    /**
     * 创建邀请列表
     * @param inviteListDTO
     */
    @PostMapping("/createInvites")
    public void createInvites(@RequestBody List<GroupApplyDTO> inviteListDTO) {
        List<GroupApply> inviteList = new ArrayList<>();
        inviteListDTO.forEach((v) -> {
            GroupApply apply = new GroupApply();
            BeanUtil.copyProperties(v, apply);
            inviteList.add(apply);
        });
        groupApplyService.createInvites(inviteList);
    }


    /**
     * 通过id查找
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public GroupApplyDTO findById(@RequestParam("id") Long id) {
        GroupApply groupApply = groupApplyService.findById(id);
        if (BeanUtil.isEmpty(groupApply)) {
            return null;
        }
        GroupApplyDTO groupApplyDTO = new GroupApplyDTO();
        BeanUtil.copyProperties(groupApply, groupApplyDTO);
        return groupApplyDTO;
    }


    /**
     * 同意申请
     * @param applyDTO
     */
    @PutMapping("/agreeApply")
    public void agreeApply(@RequestBody GroupApplyDTO applyDTO) {
        GroupApply groupApply = new GroupApply();
        BeanUtil.copyProperties(applyDTO, groupApply);
        groupApplyService.agreeApply(groupApply);
    }

    /**
     * 查询用户管理群组的申请
     * @param userId
     * @return
     */
    @GetMapping("/findGroupApplyByUserId")
    public PageResultData<GroupApplyDTO> findGroupApplyByUserId(@RequestParam("userId") Long userId, @SpringQueryMap PageParam pageParam) {
        int pageNum = pageParam.getPageNum();
        int pageSize = pageParam.getPageSize();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
                Sort.by("status").ascending()
                        .and(Sort.by("id").descending())
        );
        Page<GroupApply> groupApplyList = groupApplyService.findGroupApplyByUserId(userId, pageable);
        List<GroupApplyDTO> groupApplyDTOList = new ArrayList<>();
        for (GroupApply groupApply : groupApplyList) {
            GroupApplyDTO groupApplyDTO = new GroupApplyDTO();
            BeanUtil.copyProperties(groupApply, groupApplyDTO);
            groupApplyDTOList.add(groupApplyDTO);
        }
        return PageResultData.<GroupApplyDTO>builder()
                .total(groupApplyList.getTotalElements())
                .rows(groupApplyDTOList)
                .pageSize(pageSize)
                .pageNum(pageNum).build();
    }

}
