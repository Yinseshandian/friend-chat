package com.li.chat.controller.user;

import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author malaka
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserFeign userFeign;

    public UserController(UserFeign userFeign) {
        this.userFeign = userFeign;
    }

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list")
    @RequiresPermission(value = "user:user:list", message = "没有查看用户列表的权限")
    public ResultData list(
            UserDTO userDTO,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResultData<UserDTO> pageData = userFeign.page(userDTO, pageNum, pageSize);
        return ResultData.success(pageData);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/info/{id}")
    @RequiresPermission(value = "user:user:info", message = "没有查看用户详情的权限")
    public ResultData getInfo(@PathVariable("id") Long id) {
        UserDTO user = userFeign.findUserById(id);
        if (user == null) {
            return ResultData.error("用户不存在");
        }
        return ResultData.success(user);
    }

    @ApiOperation("添加用户")
    @PostMapping("/add")
    @RequiresPermission(value = "user:user:add", message = "没有添加用户的权限")
    public ResultData add(@Valid @RequestBody UserDTO userDTO) {
        // 检查用户名是否已存在
        UserDTO existUser = userFeign.findByUsername(userDTO.getUsername());
        if (existUser != null) {
            return ResultData.error("用户名已存在");
        }

        Long userId = userFeign.add(userDTO);
        return ResultData.success().put("userId", userId);
    }

    @ApiOperation("更新用户")
    @PutMapping("/update")
    @RequiresPermission(value = "user:user:edit", message = "没有编辑用户的权限")
    public ResultData update(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getId() == null) {
            return ResultData.error("用户ID不能为空");
        }

        // 检查用户是否存在
        UserDTO existUser = userFeign.findUserById(userDTO.getId());
        if (existUser == null) {
            return ResultData.error("用户不存在");
        }

        userFeign.update(userDTO);
        return ResultData.success();
    }

    @ApiOperation("修改用户状态")
    @PutMapping("/status")
    @RequiresPermission(value = "user:user:edit", message = "没有编辑用户的权限")
    public ResultData updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        UserDTO userDTO = userFeign.findUserById(id);
        if (userDTO == null) {
            return ResultData.error("用户不存在");
        }

        userDTO.setStatus(status);
        userFeign.update(userDTO);
        return ResultData.success();
    }
}