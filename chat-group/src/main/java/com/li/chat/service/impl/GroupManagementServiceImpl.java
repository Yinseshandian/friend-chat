package com.li.chat.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Lists;
import com.li.chat.common.enums.GroupJoinModeEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.entity.Group;
import com.li.chat.entity.GroupMember;
import com.li.chat.repository.GroupApplyRepository;
import com.li.chat.repository.GroupManagementRepository;
import com.li.chat.repository.GroupMemberRepository;
import com.li.chat.service.GroupApplyService;
import com.li.chat.service.GroupManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public Page<Group> findAllOpenByNameLike(String name, Pageable pageable) {
        return groupManagementRepository.findAllByNameLikeAndJoinModeIn("%" + name + "%", ListUtil.of(GroupJoinModeEnum.MODE_OPEN, GroupJoinModeEnum.MODE_VERIFY), pageable);
    }

    @Override
    public Page<Group> findByGroupDTO(GroupDTO groupDTO, PageParam pageParam) {
        // 创建动态查询条件
        Specification<Group> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!StringUtils.isEmpty(groupDTO.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + groupDTO.getName() + "%"));
            }

            // 状态精确查询
            if (groupDTO.getHolderUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("holderUserId"), groupDTO.getHolderUserId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 创建分页请求
        Pageable pageable = PageRequest.of(
                pageParam.getPageNum() - 1,
                pageParam.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );

        // 执行分页查询
        Page<Group> page = groupManagementRepository.findAll(specification, pageable);

        return page;
    }

    @Override
    public List<Group> findGroupByIds(Collection<Long> groupIds) {
        return groupManagementRepository.findAllById(groupIds);
    }

}
