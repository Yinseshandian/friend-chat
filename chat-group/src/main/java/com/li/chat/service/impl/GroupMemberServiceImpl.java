package com.li.chat.service.impl;

import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.entity.Group;
import com.li.chat.entity.GroupMember;
import com.li.chat.repository.GroupManagementRepository;
import com.li.chat.repository.GroupMemberRepository;
import com.li.chat.service.GroupManagementService;
import com.li.chat.service.GroupMemberService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * @author malaka
 */
@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    public GroupMemberServiceImpl(GroupMemberRepository groupMemberRepository) {
        this.groupMemberRepository = groupMemberRepository;
    }

    /**
     * 是否为群成员
     *
     * @param userId
     * @param groupId
     * @return
     */
    @Override
    public boolean isGroupMember(Long userId, Long groupId) {
        Optional<GroupMember> optional = groupMemberRepository.findOne((root, cq, cb) -> {
            Join<GroupMember, Group> join = root.join("group", JoinType.LEFT);
            Predicate eqUserId = cb.equal(root.get("userId"), userId);
            Predicate eqGroupId = cb.equal(join.get("id"), groupId);
            return cb.and(eqUserId, eqGroupId);
        });
        return optional.orElse(null) != null;
    }

    /**
     * 创建群员
     *
     * @param groupMember
     * @return id
     */
    @Override
    public Long create(GroupMember groupMember) {
        groupMemberRepository.save(groupMember);
        return groupMember.getId();
    }

    /**
     * 通过 群组id 和 用户id查找
     *
     * @param groupId
     * @param userId
     * @return
     */
    @Override
    public GroupMember findByGroupIdAndUserId(Long groupId, Long userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
    }

    /**
     * 通过群id找群用户
     *
     * @param groupId
     * @return
     */
    @Override
    public List<GroupMember> findAllByGroupId(Long groupId) {
        return groupMemberRepository.findAllByGroupId(groupId);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Override
    public int deleteById(Long id) {
        try {
            groupMemberRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e) {
            return 0;
        }
        return 1;
    }

    /**
     * 通过id查找
     *
     * @param id
     * @return
     */
    @Override
    public GroupMember findById(Long id) {
        return groupMemberRepository.findById(id).orElse(null);
    }

    /**
     * 更新
     *
     * @param groupMember
     */
    @Override
    public void update(GroupMember groupMember) {
        groupMemberRepository.save(groupMember);
    }

}
