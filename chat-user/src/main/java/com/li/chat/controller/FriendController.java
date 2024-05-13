package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.entity.Friend;
import com.li.chat.entity.User;
import com.li.chat.service.FriendService;
import io.seata.core.context.RootContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author malaka
 */
@RestController
@RequestMapping("/chat-user/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/isFriend")
    public boolean isFriend(@RequestParam("userId") Long userId,
                            @RequestParam("friendId") Long friendId) {
        return friendService.isFriend(userId, friendId);
    }

    @PostMapping("/add")
    public void add(@RequestBody FriendDTO friendDTO) {
        Friend friend = new Friend();
        BeanUtil.copyProperties(friendDTO, friend);
        friend.setUser(User.builder().id(friendDTO.getUserId()).build());
        friend.setFriend(User.builder().id(friendDTO.getFriendId()).build());
        friendService.add(friend);
    }

    @GetMapping("/list")
    public List<FriendDTO> myFriends(@RequestParam("userId")Long userId,
                                @RequestParam(value = "q", required = false) String q) {
        List<Friend> friendList = friendService.getFriendList(userId, q);
        List<FriendDTO> friendDTOList = new ArrayList<>();

        friendList.forEach(f -> {
            User user = f.getFriend();
            FriendDTO friendDTO = FriendDTO.builder()
                    .id(f.getId())
                    .friendId(user.getId())
                    .userId(f.getUser().getId())
                    .remark(f.getFriendRemark())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .build();
            friendDTOList.add(friendDTO);
        });
        return friendDTOList;
    }

    @GetMapping("/info")
    public FriendDTO info(@RequestParam("userId") Long userId,
                   @RequestParam("friendId") Long friendId) {
        System.out.println("_______________"+RootContext.getXID());
        Friend friend = friendService.friendInfo(userId, friendId);
        if (ObjectUtils.isEmpty(friend)) {
            return null;
        }
        User user = friend.getFriend();
        FriendDTO friendDTO = FriendDTO.builder()
                .id(friend.getId())
                .friendId(user.getId())
                .userId(friend.getUser().getId())
                .remark(friend.getFriendRemark())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
        return friendDTO;
    }

    @RequestMapping("/delete")
    public int deleteById(@RequestParam("id") Long id){
        return friendService.deleteById(id);
    }

    @PutMapping("/remark")
    void updateRemark(@RequestParam("userId") Long userId,
                      @RequestParam("friendId") Long friendId,
                      @RequestParam("remark") String remark) {
        friendService.updateRemark(userId,friendId, remark);
    }

}
