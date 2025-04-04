package com.li.chat.repository;

import com.li.chat.entity.Group;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author malaka
 */
public interface GroupManagementRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    @Transactional
    @Modifying
    @Query("UPDATE Group g SET g.memberNum = g.memberNum + :num WHERE g.id = :groupId")
    void addMemberNum(Long groupId, int num);

    Page<Group> findAllByNameLikeAndJoinModeIn(String name, List<Integer> joinModes, Pageable pageable);

}
