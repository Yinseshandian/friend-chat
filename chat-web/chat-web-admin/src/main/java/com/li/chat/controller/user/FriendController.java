package com.li.chat.controller.user;

import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.feign.FriendFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.param.user.FriendAddParam;
import com.li.chat.param.user.FriendRemarkParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author malaka
 */
@Api(tags = "用户好友管理接口")
@RestController
@RequestMapping("/user/friend")
public class FriendController {

    private final FriendFeign friendFeign;
    private final UserFeign userFeign;

    public FriendController(FriendFeign friendFeign, UserFeign userFeign) {
        this.friendFeign = friendFeign;
        this.userFeign = userFeign;
    }

    @ApiOperation("分页查询用户好友列表")
    @GetMapping("/list")
    @RequiresPermission(value = "user:friend:list", message = "没有查询好友列表的权限")
    public ResultData page(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResultData<FriendDTO> result = friendFeign.page(userId, q, pageNum, pageSize);
        return ResultData.success(result);
    }

    @ApiOperation("查询用户好友详情")
    @GetMapping("/info")
    @RequiresPermission(value = "user:friend:info", message = "没有查询好友详情的权限")
    public ResultData info(
            @RequestParam("userId") Long userId,
            @RequestParam("friendId") Long friendId) {

        // 检查用户是否存在
        UserDTO user = userFeign.findUserById(userId);
        if (user == null) {
            return ResultData.error("用户不存在");
        }

        // 检查好友是否存在
        UserDTO friend = userFeign.findUserById(friendId);
        if (friend == null) {
            return ResultData.error("好友不存在");
        }

        // 检查是否是好友关系
        boolean isFriend = friendFeign.isFriend(userId, friendId);
        if (!isFriend) {
            return ResultData.error("非好友关系");
        }

        // 获取好友关系详情
        FriendDTO friendDTO = friendFeign.info(userId, friendId);
        return ResultData.success(friendDTO);
    }

    @ApiOperation("添加好友关系")
    @PostMapping("/add")
    @RequiresPermission(value = "user:friend:add", message = "没有添加好友的权限")
    public ResultData add(@Valid @RequestBody FriendAddParam param) {
        // 检查用户是否存在
        UserDTO user = userFeign.findUserById(param.getUserId());
        if (user == null) {
            return ResultData.error("用户不存在");
        }

        // 检查好友是否存在
        UserDTO friend = userFeign.findUserById(param.getFriendId());
        if (friend == null) {
            return ResultData.error("好友不存在");
        }

        // 检查是否已经是好友
        boolean isFriend = friendFeign.isFriend(param.getUserId(), param.getFriendId());
        if (isFriend) {
            return ResultData.error("已经是好友关系");
        }

        // 添加好友
        FriendDTO friendDTO = new FriendDTO();
        friendDTO.setUserId(param.getUserId());
        friendDTO.setFriendId(param.getFriendId());
        friendDTO.setRemark(param.getRemark());

        friendFeign.add(friendDTO);

        return ResultData.success();
    }

    @ApiOperation("更新好友备注")
    @PutMapping("/remark")
    @RequiresPermission(value = "user:friend:edit", message = "没有修改好友备注的权限")
    public ResultData updateRemark(@Valid @RequestBody FriendRemarkParam param) {
        // 检查是否是好友关系
        boolean isFriend = friendFeign.isFriend(param.getUserId(), param.getFriendId());
        if (!isFriend) {
            return ResultData.error("非好友关系");
        }

        // 更新备注
        friendFeign.updateRemark(param.getUserId(), param.getFriendId(), param.getFriendRemark());
        friendFeign.updateRemark(param.getFriendId(), param.getUserId(), param.getUserRemark());

        return ResultData.success();
    }

    @ApiOperation("删除好友关系")
    @DeleteMapping("/delete/{id}")
    @RequiresPermission(value = "user:friend:delete", message = "没有删除好友的权限")
    public ResultData delete(@PathVariable("id") Long id) {
        int result = friendFeign.deleteById(id);
        if (result > 0) {
            return ResultData.success();
        } else {
            return ResultData.error("删除失败，好友关系不存在");
        }
    }

}