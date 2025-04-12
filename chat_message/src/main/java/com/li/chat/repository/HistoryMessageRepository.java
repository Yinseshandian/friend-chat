package com.li.chat.repository;

import com.li.chat.entity.HistoryMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author malaka
 */
public interface HistoryMessageRepository  extends JpaRepository<HistoryMessage, Long>, JpaSpecificationExecutor<HistoryMessage> {
}
