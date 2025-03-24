package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.common.utils.ResultData;
import com.li.chat.entity.Friend;
import com.li.chat.entity.User;
import com.li.chat.repository.FriendRepository;
import com.li.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * @author malaka
 */
@RestController
public class TestController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendRepository friendRepository;

    @RequestMapping("/test")
    public ResultData test() {
        User user =  userRepository.findById(10000L).orElse(null);
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO);
        List<FriendDTO> list = new LinkedList<>();
        return ResultData.success(list);
    }

    @GetMapping("/get")
    public ResultData get() {
        List<Friend> list = friendRepository.findByUserSmallIdOrUserBigId(10000L, 10000L);
        List<FriendDTO> res = new LinkedList<>();
        list.forEach((friend) -> {
            FriendDTO friendDTO = FriendDTO.builder()
                    .id(friend.getId())
                    .remark(friend.getUserBigRemark()).build();
            res.add(friendDTO);
        });

        return ResultData.success(res);
    }

}
