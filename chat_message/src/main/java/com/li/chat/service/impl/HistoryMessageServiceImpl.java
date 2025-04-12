package com.li.chat.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.li.chat.common.param.PageParam;
import com.li.chat.domain.DTO.message.MessageDTO;
import com.li.chat.entity.HistoryMessage;
import com.li.chat.repository.HistoryMessageRepository;
import com.li.chat.service.HistoryMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author malaka
 */
@Component
public class HistoryMessageServiceImpl implements HistoryMessageService {

    @Autowired
    private HistoryMessageRepository historyMessageRepository;

    @Override
    public void create(HistoryMessage message) {
        historyMessageRepository.save(message);
    }

    @Override
    public Page<HistoryMessage> findByMessageDTO(MessageDTO dto, PageParam pageParam) {
        // 创建动态查询条件
        Specification<HistoryMessage> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dto.getFromId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("fromId"), dto.getFromId()));
            }
            if (dto.getToId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("toId"), dto.getToId()));
            }
            if (dto.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), LocalDateTimeUtil.of(dto.getStartTime())));
            }

            if (dto.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), LocalDateTimeUtil.of(dto.getEndTime())));
            }
            if (dto.getMsgType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("msgType"), dto.getMsgType()));
            }
            if (dto.getTalkType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("talkType"), dto.getTalkType()));
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
        Page<HistoryMessage> page = historyMessageRepository.findAll(specification, pageable);

        return page;
    }

    @Override
    public HistoryMessage findById(Long id) {
        return historyMessageRepository.getOne(id);
    }

}
