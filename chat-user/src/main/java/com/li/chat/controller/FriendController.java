package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.entity.Friend;
import com.li.chat.entity.User;
import com.li.chat.service.FriendService;
import io.seata.core.context.RootContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
        friend.setUserSmall(User.builder().id(friendDTO.getUserId()).build());
        friend.setUserBig(User.builder().id(friendDTO.getFriendId()).build());
        friendService.add(friend);
    }

    @GetMapping("/list")
    public List<FriendDTO> list(@RequestParam("userId")Long userId,
                                @RequestParam(value = "q", required = false) String q) {
        List<Friend> friendList = friendService.getFriendList(userId, q);
        List<FriendDTO> friendDTOList = new ArrayList<>();

        friendList.forEach(f -> {
            User user = f.getUserBig();
            FriendDTO friendDTO = FriendDTO.builder()
                    .id(f.getId())
                    .friendId(user.getId())
                    .userId(f.getUserSmall().getId())
                    .remark(f.getUserBigRemark())
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
        Friend friend = friendService.friendInfo(userId, friendId);
        if (ObjectUtils.isEmpty(friend)) {
            return null;
        }
        // 保证当前用户在 userSmall 位置
        if (!Objects.equals(userId, friend.getUserSmall().getId())) {
            User userBig = friend.getUserBig();
            friend.setUserBig(friend.getUserSmall());
            friend.setUserSmall(userBig);
            // 替换好友备注
            String tRemark = friend.getUserBigRemark();
            friend.setUserBigRemark(friend.getUserSmallRemark());
            friend.setUserSmallRemark(tRemark);
        }
        User user = friend.getUserBig();
        FriendDTO friendDTO = FriendDTO.builder()
                .id(friend.getId())
                .friendId(user.getId())
                .userId(friend.getUserSmall().getId())
                .remark(friend.getUserBigRemark())
                .nickname(user.getNickname())
                .username(user.getUsername())
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
                      @RequestParam(value = "remark" ,required = false) String remark) {
        friendService.updateRemark(userId,friendId, remark);
    }

    /**
     * 分页查询用户好友列表
     */
    @GetMapping("/page")
    public PageResultData<FriendDTO> page(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) {

        PageParam pageParam = PageParam.builder().pageNum(pageNum).pageSize(pageSize).build();
        // 获取好友列表
        Page<Friend> friendPage = friendService.getFriendList(userId, q, pageParam);
        List<FriendDTO> list = friendPage.stream().map(v -> {
            User userSmall = v.getUserSmall();
            User userBig = v.getUserBig();
            FriendDTO dto = FriendDTO.builder()
                    .id(v.getId())
                    .userId(userSmall.getId())
                    .friendId(userBig.getId())
                    .userRemark(v.getUserSmallRemark())
                    .friendRemark(v.getUserBigRemark())
                    .userUsername(userSmall.getUsername())
                    .friendUsername(userBig.getUsername())
                    .build();
            return dto;
        }).collect(Collectors.toList());

        return PageResultData.<FriendDTO>builder()
                .total(friendPage.getTotalElements())
                .rows(list)
                .pageSize(pageSize)
                .pageNum(pageNum).build();
    }

}
