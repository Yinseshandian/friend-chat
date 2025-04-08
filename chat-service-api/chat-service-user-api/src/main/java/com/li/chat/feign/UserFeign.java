package com.li.chat.feign;

import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @author malaka
 */

@FeignClient(name = "chat-user", contextId = "user")
@RequestMapping("/chat-user/user")
public interface UserFeign {

    /**
     * 通过名字查询用户
     * @param username
     * @return
     */
    @GetMapping("/findByUsername")
    UserDTO findByUsername(@RequestParam("username") String username);

    /**
     * 添加用户
     * @param userDTO
     * @return
     */
    @PostMapping("/add")
    Long add(@RequestBody UserDTO userDTO);

    /**
     * 更新用户信息
     * @param userDTO
     */
    @PutMapping
    public void update(@RequestBody UserDTO userDTO);

    /**
     * 通过id查询用户
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    UserDTO findUserById(@PathVariable("id") Long id);

    /**
     * 检查用户输入的密码是否为原密码
     * @param userDTO
     * @return
     */
    @GetMapping(value = "/checkPassword")
    boolean checkPassword(@SpringQueryMap UserDTO userDTO);

    /**
     * 通过用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/checkUsernameAndPassword")
    boolean checkUsernameAndPassword(@RequestParam("username") String username,
                                      @RequestParam("password") String password);

    /**
     * 过滤不存在的用户id
     * @param userIds
     * @return
     */
    @GetMapping("/filterNotExistIds")
    List<Long> filterNotExistIds(@RequestParam("userIds") List<Long> userIds);

    /**
     * 通过id列表查询用户列表
     * @param ids
     * @return
     */
    @GetMapping("/findAllUnDelByIds")
    List<UserDTO> findAllUnDelByIds(@RequestParam("ids") Collection<Long> ids);

    /**
     * 登录
     * @param user
     * @return token
     */
    @PostMapping("/login")
    String login(@RequestBody UserDTO user);

    /**
     * 检查token
     * @param token
     * @return 用户id
     */
    @GetMapping("/checkLoginOnToken")
    Long checkLoginOnToken(@RequestParam("token") String token);

    @GetMapping("/page")
    public PageResultData<UserDTO> page(
            @SpringQueryMap UserDTO userDTO,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize);
}
