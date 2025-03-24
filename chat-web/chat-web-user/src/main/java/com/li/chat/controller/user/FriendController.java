package com.li.chat.controller.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.domain.DTO.ApplyDTO;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.common.enums.ApplyEnum;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.feign.ApplyFeign;
import com.li.chat.feign.FriendFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.param.user.FriendAgreeParam;
import com.li.chat.param.user.FriendApplyParam;
import com.li.chat.param.user.FriendUpdateRemarkParam;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author malaka
 */
@Api(tags = "0103好友相关接口")
@Slf4j
@RestController
@RequestMapping("/user/friend")
public class FriendController {

    private final FriendFeign friendFeign;


    public FriendController(FriendFeign friendFeign) {
        this.friendFeign = friendFeign;
    }


    @ApiOperation("好友列表")
    @GetMapping("/myFriends")
    public ResultData myFriends(@RequestParam(value = "q", required = false) String q) {
        Long userId = RequestContext.getUserId();
        List<FriendDTO> friends = friendFeign.list(userId, q);
        return ResultData.success(friends);
    }

    @ApiOperation("好友信息")
    @GetMapping("/info/{friendId}")
    public ResultData info(@PathVariable(value = "friendId") Long friendId) {
        Long userId = RequestContext.getUserId();
        FriendDTO friend = friendFeign.info(userId, friendId);
        if (ObjectUtils.isEmpty(friend)) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_NO_FRIEND);
        }
        return ResultData.success(friend);
    }

    @ApiOperation("删除好友")
    @GlobalTransactional
    @DeleteMapping("/delete/{friendId}")
    public ResultData delete(@PathVariable("friendId") Long friendId) {
        Long userId = RequestContext.getUserId();
        FriendDTO friend = friendFeign.info(userId, friendId);

        if (ObjectUtils.isEmpty(friend)) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_NO_FRIEND);
        }

        int num = friendFeign.deleteById(friend.getId());
        if (num == 0) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_DEL_FAIL);
        }
        log.info("用户[{}]删除好友[{}]", userId, friendId);
        return ResultData.success();
    }

    @ApiOperation("更新好友备注")
    @GlobalTransactional
    @PutMapping("/remark")
    public ResultData updateRemark(@Valid @RequestBody FriendUpdateRemarkParam param) {
        Long userId = RequestContext.getUserId();
        Long friendId = param.getFriendId();
        // 检查好友
        if (!friendFeign.isFriend(userId, friendId)) {
            return ResultData.error(WebErrorCodeEnum.USER_FRIEND_NO_FRIEND);
        }
        friendFeign.updateRemark(userId, friendId, param.getRemark());
        log.info("用户[{}]更新好友[{}]备注为[{}]", userId, friendId, param.getRemark());
        return ResultData.success();
    }
}
