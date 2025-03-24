package com.li.chat.repository;

import com.li.chat.entity.Apply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author malaka
 */
public interface ApplyRepository extends JpaRepository<Apply, Long> {

    @Modifying
    @Transactional
    Long deleteByFromIdAndToId(Long userId, Long friendId);

    Apply findByIdAndToId(Long id, Long toId);

    Page<Apply> findAllByToId(Long toId, Pageable pageable);

}
