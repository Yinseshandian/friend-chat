package com.li.chat.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.li.chat.common.param.PageParam;
import com.li.chat.entity.Friend;
import com.li.chat.entity.User;
import com.li.chat.repository.FriendRepository;
import com.li.chat.service.FriendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
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
        Friend friend = friendRepository.findByUserSmallIdAndUserBigId(userId, friendId);
        return friend != null;
    }

    @Override
    public void add(Friend friend) {
        User u = friend.getUserSmall();
        User f = friend.getUserBig();
        if (u.getId() > f.getId()) {
            friend.setUserSmall(f);
            friend.setUserBig(u);
            String temp = friend.getUserSmallRemark();
            friend.setUserSmallRemark(friend.getUserBigRemark());
            friend.setUserBigRemark(temp);
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
                onList.add(criteriaBuilder.equal(root.get("userBig").get("id"), join.get("id")));
                onList.add(criteriaBuilder.equal(root.get("userSmall").get("id"), join.get("id")));
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
                    Predicate notEqFid = criteriaBuilder.notEqual(root.get("userBig").get("id"), userId);
                    Predicate likeFriendRemark = criteriaBuilder.like(root.get("userBigRemark"), queryLikeStr);
                    Predicate likeFriendRemarkAndNotEqUserId = criteriaBuilder.and(notEqFid, likeFriendRemark);

                    Predicate notEqUid = criteriaBuilder.notEqual(root.get("userSmall").get("id"), userId);
                    Predicate likeUserRemark = criteriaBuilder.like(root.get("userSmallRemark"), queryLikeStr);
                    Predicate likeUserRemarkAndNotEqFriendId = criteriaBuilder.and(notEqUid, likeUserRemark);

                    orList.add(criteriaBuilder.or(likeFriendRemarkAndNotEqUserId, likeUserRemarkAndNotEqFriendId));

                    if (NumberUtil.isNumber(q)) {
                        // 模糊查询好友id，并且好友不是自己
                        Predicate likeUserId = criteriaBuilder.like(root.get("userSmall").get("id").as(String.class), queryLikeStr);
                        Predicate likeUserIdAndNotEqUid = criteriaBuilder.and(likeUserId, notEqUid);

                        Predicate likeFriendId = criteriaBuilder.like(root.get("userBig").get("id").as(String.class), queryLikeStr);
                        Predicate likeFriendIdAndNotEqFid = criteriaBuilder.and(likeFriendId, notEqFid);

                        orList.add(criteriaBuilder.or(likeUserIdAndNotEqUid, likeFriendIdAndNotEqFid));
                    }
                    predicates.add(criteriaBuilder.or(orList.toArray(new Predicate[orList.size()])));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        } );
        List<Friend> friendList = friendRepository.findAll(specification);
        friendList.forEach((f) -> {
            // 让当前用户总是在数据的user位置
            if (Objects.equals(userId, f.getUserBig().getId())) {
                // 替换好友信息
                User tFriend = f.getUserBig();
                f.setUserBig(f.getUserSmall());
                f.setUserSmall(tFriend);
                // 替换好友备注
                String tRemark = f.getUserBigRemark();
                f.setUserBigRemark(f.getUserSmallRemark());
                f.setUserSmallRemark(tRemark);
            }
        });
        return friendList;
    }

    @Override
    public Page<Friend> getFriendList(Long userId, String q, PageParam pageParam) {
        // 查询该用户的好友
        Specification<Friend> specification = Specification.where(new Specification<Friend>(){

            @Override
            public Predicate toPredicate(Root<Friend> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                criteriaQuery.distinct(true);
                List<Predicate> predicates = new ArrayList<>();
                Root<User> join = criteriaQuery.from(User.class);
                List<Predicate> onList = new ArrayList<>();
                onList.add(criteriaBuilder.equal(root.get("userBig").get("id"), join.get("id")));
                onList.add(criteriaBuilder.equal(root.get("userSmall").get("id"), join.get("id")));

                predicates.add(criteriaBuilder.or(onList.toArray(new Predicate[0])));

                if (userId != null) {
                    predicates.add(criteriaBuilder.equal(join.get("id"), userId));
                }
                /**
                 * 模糊搜索
                 */
                if (StringUtils.isNotEmpty(q)) {
                    String queryLikeStr = "%" + q + "%";
                    List<Predicate> orList = new ArrayList<>();
                    // 用户名模糊
                    orList.add(criteriaBuilder.like(join.get("username"), queryLikeStr));

                    Predicate likeFriendRemark = criteriaBuilder.like(root.get("userBigRemark"), queryLikeStr);

                    Predicate likeUserRemark = criteriaBuilder.like(root.get("userSmallRemark"), queryLikeStr);

                    orList.add(criteriaBuilder.or(likeFriendRemark, likeUserRemark));

                    predicates.add(criteriaBuilder.or(orList.toArray(new Predicate[0])));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        } );
        // 创建分页请求
        Pageable pageable = PageRequest.of(
                pageParam.getPageNum() - 1,
                pageParam.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );
        Page<Friend> friendPage = friendRepository.findAll(specification, pageable);
        friendPage.forEach((f) -> {
            // 让当前用户总是在数据的user位置
            if (Objects.equals(userId, f.getUserBig().getId())) {
                // 替换好友信息
                User tFriend = f.getUserBig();
                f.setUserBig(f.getUserSmall());
                f.setUserSmall(tFriend);
                // 替换好友备注
                String tRemark = f.getUserBigRemark();
                f.setUserBigRemark(f.getUserSmallRemark());
                f.setUserSmallRemark(tRemark);
            }
        });
        return friendPage;
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
        Friend friend = friendRepository.findByUserSmallIdAndUserBigId(userId, friendId);
        if (friend == null) {
            return null;
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
            // 小于时，用户为userSmall，好友为userBig
            friend.setUserBigRemark(remark);
        }else {
            // 大于时，用户为userBig，好友为userSmall
            friend.setUserSmallRemark(remark);
        }
        friendRepository.save(friend);
    }
}
