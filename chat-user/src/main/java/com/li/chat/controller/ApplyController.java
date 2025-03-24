package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.ApplyDTO;
import com.li.chat.common.enums.ApplyEnum;
import com.li.chat.entity.Apply;
import com.li.chat.entity.Friend;
import com.li.chat.entity.User;
import com.li.chat.service.ApplyService;
import com.li.chat.service.FriendService;
import com.li.chat.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        // userSmall id永远比 userBig 小
        if(fromId < toId) {
            friend.setUserSmall(User.builder().id(fromId).build());
            friend.setUserSmallRemark(toRemark);
            friend.setUserBig(User.builder().id(toId).build());
            friend.setUserBigRemark(fromRemark);
        }else {
            friend.setUserSmall(User.builder().id(toId).build());
            friend.setUserSmallRemark(fromRemark);
            friend.setUserBig(User.builder().id(fromId).build());
            friend.setUserBigRemark(toRemark);
        }
        friendService.add(friend);
    }

    @GetMapping("/applyToMe")
    public PageResultData<ApplyDTO> applyToMe(@RequestParam("userId") Long userId, @SpringQueryMap PageParam pageParam) {
        int pageNum = pageParam.getPageNum();
        int pageSize = pageParam.getPageSize();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("id").descending());
        Page<Apply> applyPage = applyService.applyToMe(userId, pageable);
        List<ApplyDTO> applyDTOList = new ArrayList<>();
        applyPage.forEach(v -> {
            ApplyDTO applyDTO = ApplyDTO.builder().build();
            BeanUtils.copyProperties(v, applyDTO);
            applyDTOList.add(applyDTO);

        });
        return PageResultData.<ApplyDTO>builder()
                .total(applyPage.getTotalElements())
                .rows(applyDTOList)
                .pageSize(pageSize)
                .pageNum(pageNum).build();
    }

}
