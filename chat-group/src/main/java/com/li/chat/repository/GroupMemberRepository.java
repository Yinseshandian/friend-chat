package com.li.chat.repository;

import com.li.chat.entity.GroupApply;
import com.li.chat.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author malaka
 */
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long>, JpaSpecificationExecutor<GroupMember> {

    /**
     * 过滤不存在的用户
     * @param userIds
     * @return
     */
    @Query("SELECT gm.userId FROM  GroupMember gm WHERE gm.userId IN :userIds AND gm.group.id = :groupId")
    List<Long> filterIsGroupMember(@Param("userIds") List<Long> userIds,
                                   @Param("groupId") Long groupId);

    /**
     * 群组id和用户id查找
     * @param groupId
     * @param userId
     * @return
     */
    GroupMember findByGroupIdAndUserId(Long groupId, Long userId);

    /**
     * 查找用户的群组id
     * @param userId 用户id
     * @param typeList 成员类型列表
     * @return
     */
    @Query("SELECT gm.group.id FROM GroupMember gm WHERE gm.userId = :userId AND gm.type in :typeList")
    List<Long> findGroupIdByUserIdAndType(@Param("userId") Long userId,
                                   @Param("typeList") List<Integer> typeList
                                   );

    /**
     * 通过群id找群用户
     * @param groupId
     * @return
     */
    List<GroupMember> findAllByGroupId(Long groupId);

    /**
     * 通过群号删除成员
     * @param groupId
     */
    @Modifying
    @Transactional
    int deleteAllByGroupId(Long groupId);
}
