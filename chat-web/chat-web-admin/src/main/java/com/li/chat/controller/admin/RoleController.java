package com.li.chat.controller.admin;

import cn.hutool.core.collection.ListUtil;
import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.admin.AdminDTO;
import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.domain.admin.RoleDTO;
import com.li.chat.feign.admin.PermissionFeign;
import com.li.chat.feign.admin.RoleFeign;
import com.li.chat.param.admin.RoleCreateParam;
import com.li.chat.param.admin.RoleUpdateParam;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author malaka
 */

@Api(tags = "角色接口")
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleFeign roleFeign;
    private final PermissionFeign permissionFeign;

    public RoleController(RoleFeign roleFeign, PermissionFeign permissionFeign) {
        this.roleFeign = roleFeign;
        this.permissionFeign = permissionFeign;
    }

    @ApiOperation("创建角色")
    @PostMapping("/create")
    @GlobalTransactional
    @RequiresPermission(value = "system:role:add", message = "没有创建角色的权限")
    public ResultData create(@Valid @RequestBody RoleCreateParam param) {
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(param, roleDTO);
        roleDTO.setStatus(true);

        Long roleId = roleFeign.create(roleDTO);

        // 分配权限
        if (param.getPermissionIds() != null && !param.getPermissionIds().isEmpty()) {
            roleFeign.assignPermissions(roleId, param.getPermissionIds());
        }

        return ResultData.success().put("roleId", roleId);
    }

    @ApiOperation("更新角色")
    @PutMapping("/update")
    @GlobalTransactional
    @RequiresPermission(value = "system:role:update", message = "没有更新角色的权限")
    public ResultData update(@Valid @RequestBody RoleUpdateParam param) {
        Long roleId = param.getId();
        RoleDTO roleDTO = roleFeign.getById(roleId);
        if (roleDTO == null) {
            return ResultData.error("角色不存在");
        }

        // 更新基本信息
        BeanUtils.copyProperties(param, roleDTO);
        roleFeign.update(roleDTO);

        // 更新权限
        if (param.getPermissionIds() != null) {
            roleFeign.assignPermissions(roleId, param.getPermissionIds());
        }

        return ResultData.success();
    }

    @ApiOperation("获取角色列表")
    @GetMapping("/list")
    @RequiresPermission(value = "system:role:list", message = "没有获取角色列表的权限")
    public ResultData list(@SpringQueryMap RoleDTO roleDTO, @SpringQueryMap PageParam pageParam) {
        PageResultData<RoleDTO> pageResultData = roleFeign.search(roleDTO, pageParam.getPageNum(), pageParam.getPageSize());
        pageResultData.setCode(200);

        return ResultData.success(pageResultData);
    }

    @ApiOperation("获取所有角色")
    @GetMapping("/all")
    @RequiresPermission(value = "system:role:all", message = "没有获取所有角色的权限")
    public ResultData all() {
        List<RoleDTO> roles = roleFeign.list();
        return ResultData.success(roles);
    }

    @ApiOperation("获取角色详情")
    @GetMapping("/info/{id}")
    @RequiresPermission(value = "system:role:info", message = "没有获取角色详情的权限")
    public ResultData info(@PathVariable("id") Long id) {
        RoleDTO roleDTO = roleFeign.getById(id);
        if (roleDTO == null) {
            return ResultData.error("角色不存在");
        }

        // 获取权限

        List<PermissionDTO> permissions = permissionFeign.getByRoleIds(ListUtil.of(id));
        roleDTO.setPermissions(permissions);

        return ResultData.success(roleDTO);
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/delete/{id}")
    @GlobalTransactional
    @RequiresPermission(value = "system:role:delete", message = "没有删除角色的权限")
    public ResultData delete(@PathVariable("id") Long id) {
        roleFeign.delete(id);
        return ResultData.success();
    }
}