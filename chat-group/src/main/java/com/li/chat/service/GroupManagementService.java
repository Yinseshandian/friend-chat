package com.li.chat.service;

import com.li.chat.entity.Group;

import java.util.List;

/**
 * @author malaka
 */
public interface GroupManagementService {

    /**
     * 创建群组
     * @param group
     */
    void create(Group group);


    /**
     * id查找群
     * @param groupId
     * @return
     */
    Group findGroupById(Long groupId);

    /**
     * 通过用户id查找群组
     * @param userId
     * @return
     */
    List<Group> findAllGroupByUserId(Long userId);

    /**
     * 添加成员数
     * @param groupId
     * @param num
     */
    void addMemberNum(Long groupId, int num);

    /**
     * 通过id删除
     * @param id
     */
    int deleteById(Long id);

    void update(Group group);
}
