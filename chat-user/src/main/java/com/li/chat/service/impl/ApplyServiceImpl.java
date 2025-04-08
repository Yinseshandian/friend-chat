package com.li.chat.service.impl;

import com.li.chat.common.enums.ApplyEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.domain.DTO.ApplyDTO;
import com.li.chat.entity.Apply;
import com.li.chat.repository.ApplyRepository;
import com.li.chat.service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author malaka
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ApplyServiceImpl implements ApplyService {

    @Autowired
    private ApplyRepository applyRepository;

    @Override
    public void apply(Apply apply) {
        applyRepository.deleteByFromIdAndToId(apply.getFromId(), apply.getToId());
        applyRepository.save(apply);
    }

    @Override
    public Apply agree(Long id) {
        Optional<Apply> optional = applyRepository.findById(id);
        optional.ifPresent(apply -> {
            apply.setStatus(ApplyEnum.STATUS_AGREE);
            applyRepository.save(apply);
        });
        return optional.orElse(null);
    }

    @Override
    public Apply findByIdAndToId(Long id, Long toId) {
        return applyRepository.findByIdAndToId(id, toId);
    }

    @Override
    public Page<Apply> applyToMe(Long userId, Pageable pageable) {
        return  applyRepository.findAllByToId(userId, pageable);
    }

    @Override
    public Page<Apply> list(ApplyDTO queryParam, PageParam pageParam) {
        // 创建动态查询条件
        Specification<Apply> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!StringUtils.isEmpty(queryParam.getFromId())) {
                predicates.add(criteriaBuilder.equal(root.get("fromId"), queryParam.getFromId()));
            }
            if (!StringUtils.isEmpty(queryParam.getToId())) {
                predicates.add(criteriaBuilder.equal(root.get("toId"), queryParam.getToId()));
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
        Page<Apply> applyPage = applyRepository.findAll(specification, pageable);

        return applyPage;
    }

}
