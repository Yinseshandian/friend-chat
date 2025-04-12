package com.li.chat.service;

import com.li.chat.common.param.PageParam;
import com.li.chat.domain.DTO.message.MessageDTO;
import com.li.chat.entity.HistoryMessage;
import org.springframework.data.domain.Page;

/**
 * @author malaka
 */
public interface HistoryMessageService {
    void create(HistoryMessage message);

    Page<HistoryMessage> findByMessageDTO(MessageDTO messageDTO, PageParam pageParam);

    HistoryMessage findById(Long id);
}
