package com.li.chat.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.fhs.jpa.wrapper.LambdaQueryWrapper;
import com.li.chat.entity.Friend;
import com.li.chat.entity.User;
import com.li.chat.repository.FriendRepository;
import com.li.chat.service.FriendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.*;

/**
 * @author malaka
 */
@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;

    public FriendServiceImpl(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        // 把id小的放前面
        if (userId > friendId) {
            Long temp = userId;
            userId = friendId;
            friendId = temp;
        }
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        return friend != null;
    }

    @Override
    public void add(Friend friend) {
        User u = friend.getUser();
        User f = friend.getFriend();
        if (u.getId() > f.getId()) {
            friend.setUser(f);
            friend.setFriend(u);
            String temp = friend.getUserRemark();
            friend.setUserRemark(friend.getFriendRemark());
            friend.setFriendRemark(temp);
        }
        if (isFriend(u.getId(), f.getId())) {
            return;
        }
        friendRepository.save(friend);
    }

    @Override
    public List<Friend> getFriendList(Long userId, String q) {
        // 查询该用户的好友
        Specification<Friend> specification = Specification.where(new Specification<Friend>(){

            @Override
            public Predicate toPredicate(Root<Friend> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                Root<User> join = criteriaQuery.from(User.class);
                List<Predicate> onList = new ArrayList<>();
                // userId 和 friendId 都为on条件
                onList.add(criteriaBuilder.equal(root.get("friend").get("id"), join.get("id")));
                onList.add(criteriaBuilder.equal(root.get("user").get("id"), join.get("id")));
                predicates.add(criteriaBuilder.or(onList.toArray(new Predicate[onList.size()])));

                predicates.add(criteriaBuilder.equal(join.get("id"), userId));
                /**
                 * 模糊搜索
                 */
                if (StringUtils.isNotEmpty(q)) {
                    String queryLikeStr = "%" + q + "%";
                    List<Predicate> orList = new ArrayList<>();
                    // 用户名模糊
                    orList.add(criteriaBuilder.like(join.get("username"), queryLikeStr));
                    // 昵称模糊
                    orList.add(criteriaBuilder.like(join.get("nickname"), queryLikeStr));

                    // 模糊查询好友备注，并且不是当前用户
                    Predicate notEqFid = criteriaBuilder.notEqual(root.get("friend").get("id"), userId);
                    Predicate likeFriendRemark = criteriaBuilder.like(root.get("friendRemark"), queryLikeStr);
                    Predicate likeFriendRemarkAndNotEqUserId = criteriaBuilder.and(notEqFid, likeFriendRemark);

                    Predicate notEqUid = criteriaBuilder.notEqual(root.get("user").get("id"), userId);
                    Predicate likeUserRemark = criteriaBuilder.like(root.get("userRemark"), queryLikeStr);
                    Predicate likeUserRemarkAndNotEqFriendId = criteriaBuilder.and(notEqUid, likeUserRemark);

                    orList.add(criteriaBuilder.or(likeFriendRemarkAndNotEqUserId, likeUserRemarkAndNotEqFriendId));

                    if (NumberUtil.isNumber(q)) {
                        // 模糊查询好友id，并且好友不是自己
                        Predicate likeUserId = criteriaBuilder.like(root.get("user").get("id").as(String.class), queryLikeStr);
                        Predicate likeUserIdAndNotEqUid = criteriaBuilder.and(likeUserId, notEqUid);

                        Predicate LikeFriendId = criteriaBuilder.like(root.get("friend").get("id").as(String.class), queryLikeStr);
                        Predicate LikeFriendIdAndNotEqFid = criteriaBuilder.and(LikeFriendId, notEqFid);

                        orList.add(criteriaBuilder.or(likeUserIdAndNotEqUid, LikeFriendIdAndNotEqFid));
                    }
                    predicates.add(criteriaBuilder.or(orList.toArray(new Predicate[orList.size()])));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        } );
        List<Friend> friendList = friendRepository.findAll(specification);
        friendList.forEach((f) -> {
            // 让当前用户总是在数据的user位置
            if (Objects.equals(userId, f.getFriend().getId())) {
                // 替换好友信息
                User tFriend = f.getFriend();
                f.setFriend(f.getUser());
                f.setUser(tFriend);
                // 替换好友备注
                String tRemark = f.getFriendRemark();
                f.setFriendRemark(f.getUserRemark());
                f.setUserRemark(tRemark);
            }
        });
        return friendList;
    }

    @Override
    public Friend friendInfo(Long userId, Long friendId) {
        Long tUserId = userId;
        // 把id小的放前面
        if (userId > friendId) {
            Long temp = userId;
            userId = friendId;
            friendId = temp;
        }
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null) {
            return null;
        }
        if (!Objects.equals(tUserId, friend.getUser().getId())) {
            User tFriend = friend.getFriend();
            friend.setFriend(friend.getUser());
            friend.setUser(tFriend);
            // 替换好友备注
            String tRemark = friend.getFriendRemark();
            friend.setFriendRemark(friend.getUserRemark());
            friend.setUserRemark(tRemark);
        }
        return friend;
    }

    @Override
    public int deleteById(Long id) {
        try{
            friendRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e) {
            return 0;
        }
        return 1;
    }

    @Override
    public void updateRemark(Long userId, Long friendId, String remark) {
        Friend friend = friendInfo(userId, friendId);

        if (userId < friendId) {
            // 小于时 user 对应的好友 注释为 friendRemark
            friend.setFriendRemark(remark);
        }else {
            // 大于时 friend 对应的好友 注释为 userRemark
            friend.setUserRemark(remark);
        }
        friendRepository.save(friend);
    }
}
