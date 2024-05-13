package com.li.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.common.enums.UserEnum;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.entity.User;
import com.li.chat.repository.UserRepository;
import com.li.chat.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author malaka
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RedisCache redisCache;

    public UserServiceImpl(UserRepository userRepository, RedisCache redisCache) {
        this.userRepository = userRepository;
        this.redisCache = redisCache;
    }

    /**
     * 通过名字查询用户
     *
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    @Override
    public Long add(User user) {
        userRepository.save(user);
        return user.getId();
    }

    /**
     * 通过id查询用户
     *
     * @param id
     * @return
     */
    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @Override
    public void update(User user) {
        if (ObjectUtils.isEmpty(user.getId())) {
            throw  new RuntimeException("更新失败，未找到该用户。");
        }
        User oldUser = userRepository.findById(user.getId()).orElse(null);
        if (ObjectUtils.isEmpty(oldUser)) {
            throw  new RuntimeException("更新失败，未找到该用户。");
        }
        BeanUtil.copyProperties(user, oldUser, CopyOptions.create().setIgnoreNullValue(true));
        userRepository.save(oldUser);
    }

    @Override
    public User findUserByIdAndPassword(Long id, String password) {
        return userRepository.findByIdAndPassword(id, password);
    }

    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        return userRepository.findUserByUsernameAndPassword(username, password);
    }

    /**
     * 过滤不存在的用户id
     *
     * @param userIds
     * @return
     */
    @Override
    public List<Long> filterNotExistIds(List<Long> userIds) {
        List<User> users = userRepository.findAllByIdInAndStatusIn(userIds, Arrays.asList(UserEnum.STATUS_OK, UserEnum.STATUS_FREEZE));
        ArrayList<Long> ids = new ArrayList<>();
        users.stream()
                .filter(v -> ObjectUtil.notEqual(v.getStatus(), UserEnum.STATUS_DEL))
                .forEach(v->{
            ids.add(v.getId());
        });
        return ids;
    }

    /**
     * 通过id列表查询用户列表
     * @param ids
     * @return
     */
    @Override
    public List<User> findAllByIds(List<Long> ids, List<Integer> status) {
        return userRepository.findAllByIdInAndStatusIn(ids, status);
    }

    /**
     * 使用id登录
     *
     * @param id
     * @return token
     */
    @Override
    public String loginById(Long id) {
        String idToTokenKey = RedisCachePrefixEnum.USER_AUTH_LOGIN_ID_TO_TOKEN + id;
        // 删除老 token
        String oldToken = redisCache.getCacheObject(idToTokenKey);
        if (oldToken != null) {
            redisCache.deleteObject(oldToken);
        }
        // 创建新 token
        String uuid = UUID.randomUUID().toString();
        String token = RedisCachePrefixEnum.USER_AUTH_LOGIN_TOKEN_TO_ID + uuid;
        // 保存 token 对 id
        redisCache.setCacheObject(token, new Date(), 7, TimeUnit.DAYS);
        redisCache.setCacheObject(token, id, 7, TimeUnit.DAYS);
        // 保存 id登录的token信息
        redisCache.setCacheObject(idToTokenKey, token,15, TimeUnit.DAYS);

        return uuid;
    }

    /**
     * 检查登录
     *
     * @param token
     * @return 用户id
     */
    @Override
    public Long checkLoginOnToken(String token) {
        Number id = redisCache.getCacheObject(RedisCachePrefixEnum.USER_AUTH_LOGIN_TOKEN_TO_ID + token);
        if (id == null) {
            return null;
        }
        return Long.parseLong(id + "");
    }
}
