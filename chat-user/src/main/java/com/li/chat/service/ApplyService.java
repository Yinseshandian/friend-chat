package com.li.chat.service;

import com.li.chat.entity.Apply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author malaka
 */
public interface ApplyService {

    void apply(Apply apply);

    Apply agree(Long id);

    Apply findByIdAndToId(Long id, Long toId);

    Page<Apply> applyToMe(Long userId, Pageable pageable);
}
