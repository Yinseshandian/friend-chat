package com.li.chat.service;

import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.entity.Group;
import com.li.chat.entity.GroupApply;

import java.util.List;

/**
 * @author malaka
 */
public interface GroupApplyService {

    /**
     * 创建申请
     * @param groupApply
     * @return
     */
    Long createApply(GroupApply groupApply);

    /**
     * 创建邀请列表
     * @param inviteList
     */
    void createInvites(List<GroupApply> inviteList);

    /**
     * 通过id查找
     * @param applyId
     * @return
     */
    GroupApply findById(Long applyId);

    /**
     * 同意申请
     * @param groupApply
     */
    void agreeApply(GroupApply groupApply);

    /**
     * 查询用户管理群组的申请
     * @param userId
     * @return
     */
    List<GroupApply> findGroupApplyByUserId(Long userId);


}
