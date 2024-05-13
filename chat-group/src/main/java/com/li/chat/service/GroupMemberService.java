package com.li.chat.service;

import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.entity.Group;
import com.li.chat.entity.GroupMember;

import java.util.List;

/**
 * @author malaka
 */
public interface GroupMemberService {

    /**
     * 是否为群成员
     * @param userId
     * @param groupId
     * @return
     */
    boolean isGroupMember(Long userId, Long groupId);

    /**
     * 创建群员
     * @param groupMember
     * @return id
     */
    Long create(GroupMember groupMember);

    /**
     * 通过 群组id 和 用户id查找
     * @param groupId
     * @param userId
     * @return
     */
    GroupMember findByGroupIdAndUserId(Long groupId, Long userId);

    /**
     * 通过群id找群用户
     * @param groupId
     * @return
     */
    List<GroupMember> findAllByGroupId(Long groupId);

    /**
     * 通过id删除
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    GroupMember findById(Long id);

    /**
     * 更新
     * @param groupMember
     */
    void update(GroupMember groupMember);
}
