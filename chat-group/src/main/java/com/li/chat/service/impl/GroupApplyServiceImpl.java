package com.li.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.li.chat.common.enums.GroupApplyEnum;
import com.li.chat.common.enums.GroupMemberTypeEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.entity.Group;
import com.li.chat.entity.GroupApply;
import com.li.chat.repository.GroupApplyRepository;
import com.li.chat.repository.GroupMemberRepository;
import com.li.chat.service.GroupApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@Slf4j
@Service
public class GroupApplyServiceImpl implements GroupApplyService {

    private final GroupApplyRepository groupApplyRepository;

    private final GroupMemberRepository groupMemberRepository;

    public GroupApplyServiceImpl(GroupApplyRepository groupApplyRepository, GroupMemberRepository groupMemberRepository) {
        this.groupApplyRepository = groupApplyRepository;
        this.groupMemberRepository = groupMemberRepository;
    }


    /**
     * 创建申请
     *
     * @param groupApply
     * @return
     */
    @Override
    public Long createApply(GroupApply groupApply) {
        int num = groupApplyRepository.deleteByGroupIdAndUserIdIn(groupApply.getGroupId(), Arrays.asList(groupApply.getUserId()));
        groupApplyRepository.save(groupApply);
        return groupApply.getId();
    }

    /**
     * 创建邀请列表
     *
     * @param inviteList
     */
    @Override
    public void createInvites(List<GroupApply> inviteList) {
        List<Long> userIds = new ArrayList<>();
        Long groupId = inviteList.get(0).getGroupId();
        for (GroupApply apply : inviteList) {
            userIds.add(apply.getUserId());
        }
        log.info("邀请用户列表：{}", userIds);
        // 过滤群组成员
        List<Long> filterIsMember = groupMemberRepository.filterIsGroupMember(userIds, groupId);
        inviteList = inviteList.stream().filter(v -> !filterIsMember.contains(v.getUserId())).collect(Collectors.toList());
        userIds = userIds.stream().filter(filterIsMember::contains).collect(Collectors.toList());
        log.info("过滤存在群成员后：{}", userIds);
        // 删除已有申请
        int num = groupApplyRepository.deleteByGroupIdAndUserIdInAndInviteUserIdIsNotNull(groupId, userIds);
        log.info("删除存在的邀请数：{}", num);
        groupApplyRepository.saveAll(inviteList);
    }

    /**
     * 通过id查找
     *
     * @param id
     * @return
     */
    @Override
    public GroupApply findById(Long id) {
        return groupApplyRepository.findById(id).orElse(null);
    }

    /**
     * 同意申请
     *
     * @param groupApply
     */
    @Override
    public void agreeApply(GroupApply groupApply) {
        groupApply.setStatus(GroupApplyEnum.STATUS_AGREE);
        groupApplyRepository.save(groupApply);
    }

    /**
     * 查询用户管理群组的申请
     *
     * @param userId
     * @return
     */
    @Override
    public Page<GroupApply> findGroupApplyByUserId(Long userId, Pageable pageable) {
        // 查找用户管理群组id
        List<Long> manageGroupIdList = groupMemberRepository.findGroupIdByUserIdAndType(userId, Arrays.asList(GroupMemberTypeEnum.TYPE_MANAGER, GroupMemberTypeEnum.TYPE_MASTER));
        return groupApplyRepository.findAllByGroupIdIn(manageGroupIdList, pageable);
    }

    @Override
    public Page<GroupApply> findByGroupApplyDTO(GroupApplyDTO dto, PageParam pageParam) {
        // 创建动态查询条件
        Specification<GroupApply> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dto.getGroupId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("groupId"), dto.getGroupId()));
            }
            if (dto.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), dto.getUserId()));
            }
            if (dto.getInviteUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("inviteUserId"), dto.getInviteUserId()));
            }
            if (dto.getProcessedBy() != null) {
                predicates.add(criteriaBuilder.equal(root.get("processedBy"), dto.getProcessedBy()));
            }
            if (dto.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), dto.getStatus()));
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
        Page<GroupApply> page = groupApplyRepository.findAll(specification, pageable);

        return page;
    }

    @Override
    public void update(GroupApply groupApply) {
        Long id = groupApply.getId();
        if (ObjectUtils.isEmpty(groupApply.getId())) {
            throw  new RuntimeException("更新失败，未找到该申请。");
        }
        GroupApply oldApply = groupApplyRepository.findById(groupApply.getId()).orElse(null);
        if (ObjectUtils.isEmpty(oldApply)) {
            throw  new RuntimeException("更新失败，未找到该申请。");
        }
        BeanUtil.copyProperties(groupApply, oldApply, CopyOptions.create().setIgnoreNullValue(true));
        groupApplyRepository.save(oldApply);
    }

}
