package com.li.chat.service;

import com.li.chat.common.param.PageParam;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.entity.User;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

/**
 * @author malaka
 */
public interface UserService {

    /**
     * 通过名字查询用户
     * @param username
     * @return
     */
    public User findByUsername(String username);

    /**
     * 添加用户
     * @param user
     * @return
     */
    public Long add(User user);

    /**
     * 通过id查询用户
     * @param id
     * @return
     */
    public User findUserById(Long id) ;

    /**
     * 更新用户
     * @param user
     * @return
     */
    void update(User user);

    /**
     * 通过id密码查询
     * @param id
     * @param password
     * @return
     */
    User findUserByIdAndPassword(Long id, String password);

    /**
     * 通过用户名和密码查询
     * @param username
     * @param password
     * @return
     */
    User findUserByUsernameAndPassword(String username, String password);

    /**
     * 过滤不存在的用户id
     * @param userIds
     * @return
     */
    List<Long> filterNotExistIds(List<Long> userIds);
    /**
     * 通过id列表查询用户列表
     * @param ids
     * @param status
     * @return
     */
    List<User> findAllByIds(Collection<Long> ids, Collection<Integer> status);

    /**
     * 使用id登录
     * @param id
     * @return token
     */
    String loginById(Long id);

    /**
     * 检查登录
     * @param token
     * @return 用户id
     */
    Long checkLoginOnToken(String token);

    Page<User> page(UserDTO userDTO, PageParam pageParam);
}
