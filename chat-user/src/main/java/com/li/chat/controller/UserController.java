package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.enums.UserEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.ApplyDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.entity.User;
import com.li.chat.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@RestController
@RequestMapping("/chat-user/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/findByUsername")
    public UserDTO findByUsername(@RequestParam("username") String username) {
        User user = userService.findByUsername(username);
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @PostMapping("/add")
    public Long add(@RequestBody UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return userService.add(user);
    }

    @PutMapping
    public void update(@RequestBody UserDTO userDTO) {
        if (ObjectUtils.isEmpty(userDTO.getId())) {
            throw new RuntimeException("更新失败，未找到该用户。");
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userService.update(user);
    }

    @GetMapping("/{id}")
    public UserDTO findUserById(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }
        System.out.println(user.getPassword());
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @GetMapping("/checkPassword")
    public boolean checkPassword(UserDTO userDTO) {
        User user = userService.findUserByIdAndPassword(userDTO.getId(), userDTO.getPassword());
        return !ObjectUtils.isEmpty(user);
    }

    @GetMapping("/checkUsernameAndPassword")
    public boolean checkUsernameAndPassword(@RequestParam("username") String username,
                                      @RequestParam("password") String password) {
        return !ObjectUtils.isEmpty(userService.findUserByUsernameAndPassword(username, password));
    }

    /**
     * 过滤不存在的用户id
     * @param userIds
     * @return
     */
    @GetMapping("/filterNotExistIds")
    public List<Long> filterNotExistIds(@RequestParam("userIds") List<Long> userIds) {
        return userService.filterNotExistIds(userIds);
    }

    /**
     * 通过id列表查询用户列表，未注销账号
     * @param ids
     * @return
     */
    @GetMapping("/findAllUnDelByIds")
    public List<UserDTO> findAllUnDelByIds(@RequestParam("ids") Collection<Long> ids) {
        List<User> userList = userService.findAllByIds(ids, Arrays.asList(UserEnum.STATUS_OK, UserEnum.STATUS_FREEZE));
        return userList.stream().map(v -> {
            UserDTO userDTO = new UserDTO();
            BeanUtil.copyProperties(v, userDTO);
            return userDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 登录
     * @param userDTO
     * @return token
     */
    @PostMapping("/login")
    public String login(@RequestBody UserDTO userDTO) {
        return userService.loginById(userDTO.getId());
    }

    @GetMapping("/checkLoginOnToken")
    public Long checkLoginOnToken(@RequestParam("token") String token) {
        return userService.checkLoginOnToken(token);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public PageResultData<UserDTO> page(
            @SpringQueryMap UserDTO userDTO,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) {
        PageParam pageParam = PageParam.builder().pageNum(pageNum).pageSize(pageSize).build();
        Page<User> userPage = userService.page(userDTO, pageParam);

        List<UserDTO> list = userPage.stream().map(v -> {
            UserDTO dto = UserDTO.builder().build();
            BeanUtils.copyProperties(v, dto);
            return dto;
        }).collect(Collectors.toList());

        return PageResultData.<UserDTO>builder()
                .total(userPage.getTotalElements())
                .rows(list)
                .pageSize(pageSize)
                .pageNum(pageNum).build();
    }
}
