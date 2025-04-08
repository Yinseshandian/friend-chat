package com.li.chat.controller.admin;

import cn.hutool.crypto.digest.BCrypt;
import com.google.common.base.Objects;
import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.admin.AdminDTO;
import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.feign.admin.AdminFeign;
import com.li.chat.feign.admin.PermissionFeign;
import com.li.chat.feign.admin.RoleFeign;
import com.li.chat.param.admin.AdminCreateParam;
import com.li.chat.param.admin.AdminPasswordParam;
import com.li.chat.param.admin.AdminProfileUpdateParam;
import com.li.chat.param.admin.AdminUpdateParam;
import com.li.chat.vo.admin.AdminInfoVO;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@Api(tags = "管理员接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminFeign adminFeign;
    private final RoleFeign roleFeign;
    private final PermissionFeign permissionFeign;

    public AdminController(AdminFeign adminFeign, RoleFeign roleFeign, PermissionFeign permissionFeign) {
        this.adminFeign = adminFeign;
        this.roleFeign = roleFeign;
        this.permissionFeign = permissionFeign;
    }

    @ApiOperation("创建管理员")
    @PostMapping("/create")
    @GlobalTransactional
    @RequiresPermission(value = "system:admin:add", message = "没有创建管理员的权限")
    public ResultData create(@Valid @RequestBody AdminCreateParam param) {
        AdminDTO adminDTO = new AdminDTO();
        BeanUtils.copyProperties(param, adminDTO);
        adminDTO.setStatus(true);
        // 加密密码
        adminDTO.setPassword(BCrypt.hashpw(adminDTO.getPassword()));
        Long adminId = adminFeign.create(adminDTO);

        // 分配角色
        if (param.getRoleIds() != null && !param.getRoleIds().isEmpty()) {
            adminFeign.assignRoles(adminId, param.getRoleIds());
        }

        return ResultData.success().put("adminId", adminId);
    }

    @ApiOperation("更新管理员")
    @PutMapping("/update")
    @GlobalTransactional
    @RequiresPermission(value = "system:admin:edit", message = "没有编辑管理员的权限")
    public ResultData update(@Valid @RequestBody AdminUpdateParam param) {
        Long adminId = param.getId();
        AdminDTO adminDTO = adminFeign.getById(adminId);
        if (adminDTO == null) {
            return ResultData.error("管理员不存在");
        }

        // 更新基本信息
        BeanUtils.copyProperties(param, adminDTO);
        adminFeign.update(adminDTO);

        // 更新角色
        if (param.getRoleIds() != null) {
            adminFeign.assignRoles(adminId, param.getRoleIds());
        }

        return ResultData.success();
    }

    @ApiOperation("更新个人信息员")
    @PutMapping("/updateProfile")
    @GlobalTransactional
    public ResultData updateProfile(@Valid @RequestBody AdminProfileUpdateParam param) {
        Long userId = RequestContext.getUserId();
        AdminDTO adminDTO = adminFeign.getById(userId);
        if (adminDTO == null) {
            return ResultData.error("管理员不存在");
        }

        // 更新基本信息
        BeanUtils.copyProperties(param, adminDTO);
        adminFeign.update(adminDTO);

        return ResultData.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/updatePassword")
    @GlobalTransactional
    public ResultData updatePassword(@Valid @RequestBody AdminPasswordParam param) {
        Long adminId = RequestContext.getUserId();
        AdminDTO adminDTO = adminFeign.getById(adminId);
        if (adminDTO == null) {
            return ResultData.error("管理员不存在");
        }

        // 验证旧密码...
        if (!BCrypt.checkpw(param.getOldPassword(), adminDTO.getPassword())) {
            return ResultData.error("旧密码错误");
        }

        adminFeign.updatePassword(adminId, param.getNewPassword());
        return ResultData.success();
    }

    @ApiOperation("获取管理员列表")
    @GetMapping("/list")
    @RequiresPermission(value = "system:admin:list", message = "没有查看管理员列表的权限")
    public ResultData list(AdminDTO adminDTO, PageParam pageParam) {
        PageResultData<AdminDTO> pageResultData = adminFeign.search(adminDTO, pageParam.getPageNum(), pageParam.getPageSize());
        pageResultData.setCode(200);
        return ResultData.success(pageResultData);
    }

    @ApiOperation("获取管理员详情")
    @GetMapping("/info/{id}")
    @RequiresPermission(value = "system:admin:info", message = "没有查看管理员的权限")
    public ResultData info(@PathVariable("id") Long id) {
        AdminDTO adminDTO = adminFeign.getById(id);
        if (adminDTO == null) {
            return ResultData.error("管理员不存在");
        }

        return ResultData.success(adminDTO);
    }

    @ApiOperation("获取当前管理员信息")
    @GetMapping("/info")
    public ResultData info() {
        Long adminId = RequestContext.getUserId();
        AdminDTO adminDTO = adminFeign.getById(adminId);
        if (adminDTO == null) {
            return ResultData.error("管理员不存在");
        }

        // 获取角色
        List<Long> roleIds = adminDTO.getRoles().stream()
                .map(roleDTO -> roleDTO.getId())
                .collect(Collectors.toList());

        // 获取权限
        List<PermissionDTO> permissions = permissionFeign.getByRoleIds(roleIds);

        // 构建返回数据
        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtils.copyProperties(adminDTO, adminInfoVO);
        adminInfoVO.setPermissions(permissions);

        return ResultData.success(adminInfoVO);
    }

    @ApiOperation("删除管理员")
    @DeleteMapping("/delete/{id}")
    @GlobalTransactional
    @RequiresPermission(value = "system:admin:delete", message = "没有删除管理员的权限")
    public ResultData delete(@PathVariable("id") Long id) {
        adminFeign.delete(id);
        return ResultData.success();
    }

    @ApiOperation("启用/禁用管理员")
    @PutMapping("/updateStatus")
    @RequiresPermission(value = "system:admin:status", message = "没有删启用/禁用管理员的权限")
    public ResultData updateStatus(@RequestParam("id") Long id, @RequestParam("status") Boolean status) {
        adminFeign.updateStatus(id, status);
        return ResultData.success();
    }

}