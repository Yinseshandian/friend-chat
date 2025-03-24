package com.li.chat.service.impl;

import com.li.chat.common.enums.ApplyEnum;
import com.li.chat.entity.Apply;
import com.li.chat.repository.ApplyRepository;
import com.li.chat.service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
