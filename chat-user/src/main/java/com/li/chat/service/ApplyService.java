package com.li.chat.service;

import com.li.chat.entity.Apply;

/**
 * @author malaka
 */
public interface ApplyService {

    void apply(Apply apply);

    Apply agree(Long id);

    Apply findByIdAndToId(Long id, Long toId);
}
