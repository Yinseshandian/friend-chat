package com.li.chat.repository;

import com.li.chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 用户Repository
 * @author malaka
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * 通过名字查询用户
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 通过id和密码查询用户
     * @param id
     * @param password
     * @return
     */
    User findByIdAndPassword(Long id, String password);

    /**
     * 通过用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    User findUserByUsernameAndPassword(String username, String password);

    /**
     * id列表查找用户
     * @param userIds
     * @param status
     * @return
     */
    List<User> findAllByIdInAndStatusIn(@Param("userIds") Collection<Long> userIds,
                                        @Param("status")Collection<Integer> status);



}
