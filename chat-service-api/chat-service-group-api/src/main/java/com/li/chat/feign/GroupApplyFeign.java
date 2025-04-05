package com.li.chat.feign;

import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.domain.DTO.GroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author malaka
 */
@FeignClient(name = "chat-group", contextId="groupApply")
@RequestMapping("/chat-group/apply")
public interface GroupApplyFeign {

    /**
     * 创建
     * @param groupApplyDTO
     * @return
     */
    @PostMapping("/createApply")
    Long createApply(@RequestBody GroupApplyDTO groupApplyDTO);

    /**
     * 创建邀请列表
     * @param inviteList
     */
    @PostMapping("/createInvites")
    void createInvites(@RequestBody List<GroupApplyDTO> inviteList);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    @GetMapping("/findById")
    GroupApplyDTO findById(@RequestParam("id") Long id);

    /**
     * 同意申请
     * @param applyDTO
     */
    @PutMapping("/agreeApply")
    void agreeApply(@RequestBody GroupApplyDTO applyDTO);

    /**
     * 查询用户管理群组的申请
     * @param userId
     * @return
     */
    @GetMapping("/findGroupApplyByUserId")
    PageResultData<GroupApplyDTO> findGroupApplyByUserId(@RequestParam("userId") Long userId, @SpringQueryMap PageParam pageParam);
}
