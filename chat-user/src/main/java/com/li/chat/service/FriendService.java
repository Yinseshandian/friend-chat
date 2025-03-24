package com.li.chat.service;

import com.li.chat.entity.Apply;
import com.li.chat.entity.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author malaka
 */
public interface FriendService {

    boolean isFriend(Long userId, Long friendId);

    void add(Friend friend);

    List<Friend> getFriendList(Long userId, String q);

    Friend friendInfo(Long userId, Long friendId);

    int deleteById(Long id);

    void updateRemark(Long userId, Long friendId, String remark);

}
