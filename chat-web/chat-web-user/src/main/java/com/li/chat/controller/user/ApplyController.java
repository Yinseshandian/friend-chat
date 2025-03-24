package com.li.chat.controller.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.li.chat.common.enums.ApplyEnum;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.ApplyDTO;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.feign.ApplyFeign;
import com.li.chat.feign.FriendFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.param.user.FriendAgreeParam;
import com.li.chat.param.user.FriendApplyParam;
import com.li.chat.vo.ApplyVo;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@Api(tags = "0104好友申请相关接口")
@Slf4j
@RestController
@RequestMapping("/user/apply")
public class ApplyController {

    private final FriendFeign friendFeign;

    private final UserFeign userFeign;

    private final ApplyFeign applyFeign;

    public ApplyController(FriendFeign friendFeign, UserFeign userFeign, ApplyFeign applyFeign) {
        this.friendFeign = friendFeign;
        this.userFeign = userFeign;
        this.applyFeign = applyFeign;
    }

    @ApiOperation("申请好友")
    @GlobalTransactional
    @PostMapping("/apply")
    public ResultData apply(@RequestBody @Valid FriendApplyParam userApplyParam) {
        Long fromId = RequestContext.getUserId();
        Long toId = userApplyParam.getToId();
        UserDTO toUser = userFeign.findUserById(toId);
        // 未找到用户
        if (ObjectUtil.isEmpty(toUser)) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_USER_NOT_FOUND);
        }
        FriendDTO friend = friendFeign.info(fromId, toId);
        if (ObjectUtil.isNotEmpty(friend)) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_ALREADY_IS_FRIEND);
        }
        ApplyDTO applyDTO = new ApplyDTO();
        applyDTO.setFromId(fromId);
        BeanUtil.copyProperties(userApplyParam, applyDTO);

        applyFeign.apply(applyDTO);
        // TODO 发送消息让用户刷新申请列表
        return ResultData.success();
    }

    @ApiOperation("同意好友申请")
    @GlobalTransactional
    @PutMapping("/agree")
    public ResultData agree(@Valid @RequestBody FriendAgreeParam param) {
        Long toId = RequestContext.getUserId();
        ApplyDTO applyDTO = applyFeign.findByIdAndToId(param.getId(), toId);
        // 未找到
        if (ObjectUtils.isEmpty(applyDTO)) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_APPLY_NO_FOUND);
        }
        // 已处理
        if (!ApplyEnum.STATUS_UNTREATED.equals(applyDTO.getStatus())) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_APPLY_FINISH);
        }
        String remark = param.getRemark();
        // 备注为空使用 用户nickname

        applyFeign.agree(param.getId(), remark);
        log.info("用户[{}]同意添加好友[{}]", applyDTO.getToId(), applyDTO.getFromId());
        return ResultData.success();
    }

    @ApiOperation("好友申请列表")
    @GlobalTransactional
    @GetMapping("/list")
    public PageResultData<ApplyVo> list(PageParam param) {
        Long userId = RequestContext.getUserId();
        PageResultData<ApplyDTO> applyDTOs = applyFeign.applyToMe(userId, param);
        List<Long> uids = applyDTOs.getRows().stream().map(v -> v.getFromId()).collect(Collectors.toList());
        Map<Long, UserDTO> userIdMap = userFeign.findAllUnDelByIds(uids)
                .stream().collect(Collectors.toMap(UserDTO::getId, v -> v));

        PageResultData<ApplyVo> resultData = new PageResultData<>();
        List<ApplyVo> resList = new ArrayList<>();
        applyDTOs.getRows().forEach(v -> {
            Long fromId = v.getFromId();
            UserDTO from = userIdMap.get(fromId);
            ApplyVo applyVo = ApplyVo.builder()
                    .avatar(from.getAvatar())
                    .nickname(from.getNickname())
                    .build();
            BeanUtil.copyProperties(v, applyVo);
            resList.add(applyVo);
        });
        resultData.setPageNum(param.getPageNum());
        resultData.setPageSize(param.getPageSize());
        resultData.setTotal(applyDTOs.getTotal());
        resultData.setRows(resList);
        resultData.setCode(200);
        return resultData;
    }

    @ApiOperation("好友申请列表")
    @GlobalTransactional
    @GetMapping("/{id}")
    public ResultData<ApplyVo> applyInfo(@PathVariable("id") Long id) {
        Long userId = RequestContext.getUserId();
        ApplyDTO applyDTO = applyFeign.findByIdAndToId(id, userId);
        if (ObjectUtils.isEmpty(applyDTO)) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_APPLY_NO_FOUND);
        }
        UserDTO from = userFeign.findUserById(applyDTO.getFromId());

        ApplyVo applyVo = ApplyVo.builder().nickname(from.getNickname())
                .avatar(from.getAvatar())
                .username(from.getUsername())
                .sex(from.getSex() + "")
                .build();

        BeanUtil.copyProperties(applyDTO, applyVo);

        return ResultData.success(applyVo);
    }

}
