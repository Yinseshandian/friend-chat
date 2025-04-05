package com.li.chat.repository;

import com.li.chat.entity.Group;
import com.li.chat.entity.GroupApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author malaka
 */
public interface GroupApplyRepository extends JpaRepository<GroupApply, Long>, JpaSpecificationExecutor<GroupApply> {

    /**
     * 删除申请通过群id'和用户id
     * @param groupId
     * @param userIds
     * @return
     */
    @Modifying
    @Transactional
    int deleteByGroupIdAndUserIdIn(Long groupId, List<Long> userIds);

    /**
     * 删除邀请通过群id'和用户id
     * @param groupId
     * @param userIds
     * @return
     */
    @Modifying
    @Transactional
    int deleteByGroupIdAndUserIdInAndInviteUserIdIsNotNull(Long groupId, List<Long> userIds);

    /**
     * 查找群组的申请
     * @param groupIdList
     * @return
     */
    Page<GroupApply> findAllByGroupIdIn(List<Long> groupIdList, Pageable pageable);

    /**
     * 通过群号删除
     * @param groupId
     * @return
     */
    @Modifying
    @Transactional
    int deleteAllByGroupId(Long groupId);

}
