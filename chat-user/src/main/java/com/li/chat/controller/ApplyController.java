package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.domain.DTO.ApplyDTO;
import com.li.chat.common.enums.ApplyEnum;
import com.li.chat.entity.Apply;
import com.li.chat.entity.Friend;
import com.li.chat.entity.User;
import com.li.chat.service.ApplyService;
import com.li.chat.service.FriendService;
import com.li.chat.service.UserService;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author malaka
 */
@RestController
@RequestMapping("/chat-user/apply")
public class ApplyController {

    private final ApplyService applyService;

    private final UserService userService;

    private final FriendService friendService;

    public ApplyController(ApplyService applyService, UserService userService, FriendService friendService) {
        this.applyService = applyService;
        this.userService = userService;
        this.friendService = friendService;
    }

    @PostMapping
    public void apply(@RequestBody ApplyDTO applyDTO) {
        Long toId = applyDTO.getToId();
        User toUser = userService.findUserById(toId);
        if (ObjectUtils.isEmpty(toUser)) {
            return;
        }
        Apply apply = new Apply();
        BeanUtil.copyProperties(applyDTO, apply);
        apply.setStatus(ApplyEnum.STATUS_UNTREATED);
        applyService.apply(apply);
    }

    @GetMapping("/findByIdAndToId")
    public ApplyDTO findByIdAndToId(@RequestParam("id") Long id,
                                    @RequestParam("toId") Long toId) {
        Apply apply = applyService.findByIdAndToId(id, toId);
        if (apply == null) {
            return null;
        }
        ApplyDTO applyDTO = new ApplyDTO();
        BeanUtil.copyProperties(apply, applyDTO);
        return applyDTO;
    }

    @GetMapping("/agree")
    public void agree(@RequestParam("id") Long id, @RequestParam(value = "toRemark", required = false) String toRemark) {
        Apply apply = applyService.agree(id);
        Long fromId = apply.getFromId();
        Long toId = apply.getToId();
        String fromRemark = apply.getRemark();
        Friend friend = new Friend();
        if(fromId < toId) {
            friend.setUser(User.builder().id(fromId).build());
            friend.setFriend(User.builder().id(toId).build());
            friend.setFriendRemark(fromRemark);
            friend.setUserRemark(toRemark);
        }else {
            friend.setUser(User.builder().id(toId).build());
            friend.setFriend(User.builder().id(fromId).build());
            friend.setFriendRemark(toRemark);
            friend.setUserRemark(fromRemark);
        }
        friendService.add(friend);
    }

}
