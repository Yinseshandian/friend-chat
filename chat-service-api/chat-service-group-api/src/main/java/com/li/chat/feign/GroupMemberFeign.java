package com.li.chat.feign;

import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author malaka
 */
@FeignClient(name = "chat-group", contextId = "groupMember")
@RequestMapping("/chat-group/member")
public interface GroupMemberFeign {

    /**
     * 是否为群成员
     * @param userId
     * @param groupId
     * @return
     */
    @GetMapping("/isGroupMember")
    boolean isGroupMember(@RequestParam("userId") Long userId,
                          @RequestParam("groupId") Long groupId);


    /**
     * 创建群成员
     * @param member
     * @return
     */
    @PostMapping("/create")
    Long create(@RequestBody GroupMemberDTO member);

    /**
     * 通过用户id和群组id查找
     * @param userId
     * @param groupId
     * @return
     */
    @GetMapping("/findByGroupIdAndUserId")
    GroupMemberDTO findByGroupIdAndUserId(@RequestParam("groupId") Long groupId,
                                                 @RequestParam("userId") Long userId) ;

    /**
     * 通过群组查找群成员
     * @param groupId
     * @return
     */
    @GetMapping("/findAllByGroupId")
    List<GroupMemberDTO> findAllByGroupId(@RequestParam("groupId") Long groupId);

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById")
    int deleteById(@RequestParam("id") Long id);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    @GetMapping("/findById")
    GroupMemberDTO findById(@RequestParam("id") Long id);

    /**
     * 更新
     * @param member
     */
    @PutMapping("/update")
    void update(@RequestBody GroupMemberDTO member);
}
