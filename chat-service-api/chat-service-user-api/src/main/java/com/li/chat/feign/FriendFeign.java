package com.li.chat.feign;

import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.FriendDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author malaka
 */
@FeignClient(name = "chat-user", contextId = "userFriend")
@RequestMapping("/chat-user/friend")
public interface FriendFeign {

    /**
     * 是否为好友
     * @param userId
     * @param friendId
     * @return
     */
    @GetMapping("/isFriend")
    boolean isFriend(@RequestParam("userId") Long userId,
                            @RequestParam("friendId") Long friendId);

    /**
     * 添加好友
     * @param friendDTO
     */
    @PostMapping("/add")
    void add(@RequestBody FriendDTO friendDTO);

    /**
     * 好友列表
     * @param userId
     * @param q
     * @return
     */
    @GetMapping("/list")
    List<FriendDTO> list(@RequestParam("userId")Long userId,
                                @RequestParam(value = "q", required = false) String q);

    /**
     * 好友详情
     * @param userId
     * @param friendId
     * @return
     */
    @GetMapping("/info")
    FriendDTO info(@RequestParam("userId") Long userId,
                   @RequestParam("friendId") Long friendId);

    /**
     * 删除好友
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    int deleteById(@RequestParam("id") Long id);

    /**
     * 更新好友备注
     * @param userId
     * @param friendId
     * @param remark
     */
    @PutMapping("/remark")
    void updateRemark(@RequestParam("userId") Long userId,
                      @RequestParam("friendId") Long friendId,
                      @RequestParam(value = "remark" ,required = false) String remark);

    /**
     * 分页查询用户好友列表
     */
    @GetMapping("/page")
    PageResultData<FriendDTO> page(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) ;
}
