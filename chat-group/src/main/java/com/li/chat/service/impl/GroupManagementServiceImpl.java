package com.li.chat.service.impl;

import com.li.chat.entity.Group;
import com.li.chat.entity.GroupMember;
import com.li.chat.repository.GroupApplyRepository;
import com.li.chat.repository.GroupManagementRepository;
import com.li.chat.repository.GroupMemberRepository;
import com.li.chat.service.GroupApplyService;
import com.li.chat.service.GroupManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author malaka
 */
@Service
public class GroupManagementServiceImpl implements GroupManagementService {

    private final GroupManagementRepository groupManagementRepository;

    private final GroupMemberRepository groupMemberRepository;

    private final GroupApplyRepository groupApplyRepository;

    public GroupManagementServiceImpl(GroupManagementRepository groupManagementRepository,
                                      GroupMemberRepository groupMemberRepository,
                                      GroupApplyRepository groupApplyRepository) {
        this.groupManagementRepository = groupManagementRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupApplyRepository = groupApplyRepository;
    }

    @Override
    public void create(Group group) {
        groupManagementRepository.save(group);
    }

    /**
     * id查找群
     *
     * @param groupId
     * @return
     */
    @Override
    public Group findGroupById(Long groupId) {
        return groupManagementRepository.findById(groupId).orElse(null);
    }

    /**
     * 通过用户id查找群组
     *
     * @param userId
     * @return
     */
    @Override
    public List<Group> findAllGroupByUserId(Long userId) {
        List<Group> groupList = groupManagementRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
            Root<GroupMember> join = criteriaQuery.from(GroupMember.class);
            Predicate on = criteriaBuilder.equal(root.get("id"), join.get("group").get("id"));
            Predicate eqUserId = criteriaBuilder.equal(join.get("userId"), userId);
            return criteriaBuilder.and(on, eqUserId);
        }));
        return groupList;
    }

    /**
     * 添加成员数
     *
     * @param groupId
     * @param num
     */
    @Override
    public void addMemberNum(Long groupId, int num) {
        groupManagementRepository.addMemberNum(groupId, num);
    }

    /**
     * 通过id删除
     *
     * @param id
     */
    @Override
    public int deleteById(Long id) {
        // 删除申请
        groupApplyRepository.deleteAllByGroupId(id);
        // 删除群成员
        groupMemberRepository.deleteAllByGroupId(id);
        // 删除群组
        try{
            groupManagementRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e) {
            return 0;
        }
        return 1;
    }

    @Override
    public void update(Group group) {
        groupManagementRepository.save(group);
    }


}
