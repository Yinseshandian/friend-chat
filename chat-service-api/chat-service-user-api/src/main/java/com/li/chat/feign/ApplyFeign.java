package com.li.chat.feign;

import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.ApplyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

/**
 * @author malaka
 */
@FeignClient(name = "chat-user", contextId = "userApply")
@RequestMapping("/chat-user/apply")
public interface ApplyFeign {

    /**
     * 好友申请
     * @param applyDTO
     */
    @PostMapping
    void apply(@RequestBody ApplyDTO applyDTO);

    /**
     * 查询是否有申请
     * @param id
     * @param toId
     * @return
     */
    @GetMapping("/findByIdAndToId")
    ApplyDTO findByIdAndToId(@RequestParam("id") Long id,
                             @RequestParam("toId") Long toId);

    /**
     * 同意
     * @param id
     * @param toRemark
     */
    @GetMapping("/agree")
    void agree(@RequestParam("id") Long id, @RequestParam(value = "toRemark", required = false) String toRemark);

    /**
     * 列表
     * @param userId
     * @param pageParam
     * @return
     */
    @GetMapping("/applyToMe")
    PageResultData<ApplyDTO> applyToMe(@RequestParam("userId") Long userId, @SpringQueryMap PageParam pageParam);

    @GetMapping("/list")
    PageResultData<ApplyDTO> list(@RequestParam("fromId") Long fromId,@RequestParam("toId") Long toId, @SpringQueryMap PageParam pageParam);
}
