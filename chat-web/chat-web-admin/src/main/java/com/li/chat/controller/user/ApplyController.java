package com.li.chat.controller.user;

import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.ApplyDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.feign.ApplyFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.vo.user.ApplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@Api(tags = "好友申请管理接口")
@RestController
@RequestMapping("/user/apply")
public class ApplyController {

    private final ApplyFeign applyFeign;
    private final UserFeign userFeign;

    public ApplyController(ApplyFeign applyFeign, UserFeign userFeign) {
        this.applyFeign = applyFeign;
        this.userFeign = userFeign;
    }

    @ApiOperation("分页查询申请列表")
    @GetMapping("/list")
    @RequiresPermission(value = "user:apply:list", message = "没有查询好友申请的权限")
    public ResultData list(
            @RequestParam(required = false) Long fromId,
            @RequestParam(required = false) Long toId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 查询申请列表
        PageParam pageParam = PageParam.builder().pageNum(pageNum).pageSize(pageSize).build();
        PageResultData<ApplyDTO> pageData = applyFeign.list(fromId, toId, pageParam);

        Collection<ApplyDTO> rows = pageData.getRows();

        // 提取所有用户ID
        Set<Long> userIds = new HashSet<>();
        for (ApplyDTO apply : rows) {
            userIds.add(apply.getFromId());
            userIds.add(apply.getToId());
        }

        // 批量获取用户信息
        List<UserDTO> userList = userFeign.findAllUnDelByIds(userIds);
        Map<Long, UserDTO> userMap = userList.stream()
                .collect(Collectors.toMap(UserDTO::getId, user -> user));

        // 组装VO
        List<ApplyVO> voList = new ArrayList<>();
        for (ApplyDTO apply : rows) {
            ApplyVO vo = new ApplyVO();
            BeanUtils.copyProperties(apply, vo);

            // 设置申请人信息
            UserDTO fromUser = userMap.get(apply.getFromId());
            if (fromUser != null) {
                vo.setFromUsername(fromUser.getUsername());
                vo.setFromNickname(fromUser.getNickname());
                vo.setFromAvatar(fromUser.getAvatar());
            }

            // 设置被申请人信息
            UserDTO toUser = userMap.get(apply.getToId());
            if (toUser != null) {
                vo.setToUsername(toUser.getUsername());
                vo.setToNickname(toUser.getNickname());
                vo.setToAvatar(toUser.getAvatar());
            }

            voList.add(vo);
        }

        // 更新结果
        PageResultData<ApplyVO> result = new PageResultData<>();
        result.setRows(voList);
        result.setTotal(pageData.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return ResultData.success(result);
    }

    @ApiOperation("同意好友申请")
    @PostMapping("/agree")
    @RequiresPermission(value = "user:apply:edit", message = "没有处理好友申请的权限")
    public ResultData agree(
            @RequestParam("id") Long id,
            @RequestParam(value = "toRemark", required = false) String toRemark) {

        applyFeign.agree(id, toRemark);
        return ResultData.success();
    }

}