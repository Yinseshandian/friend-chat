package com.li.chat.repository;

import com.li.chat.entity.Friend;
import com.li.chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author malaka
 */
public interface FriendRepository extends JpaRepository<Friend, Long>, JpaSpecificationExecutor<Friend> {

    Friend findByUserIdAndFriendId(Long userId, Long friendId);

    List<Friend> findByUserIdOrFriendId(Long userId, Long friendId);

    @Query(value = "SELECT * FROM chat_user u \n" +
            "JOIN chat_friend f ON u.id = f.user_id \n" +
            "WHERE u.id = '%?1%' AND (u.username LIKE '%?2%' \n" +
            "OR u.nickname LIKE '%?2%'\n" +
            "OR f.friend_remark LIKE '%?2%'\n" +
            "OR f.friend_id LIKE '%?2%')\n" +
            "\n", nativeQuery = true)
    List<Friend> findFriendForFs(Long userId, String q);

    @Modifying
    @Transactional
    Integer removeById(Long id);

}
