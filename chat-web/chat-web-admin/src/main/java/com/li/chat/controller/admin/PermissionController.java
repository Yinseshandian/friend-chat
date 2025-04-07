package com.li.chat.controller.admin;

import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.domain.admin.RoleDTO;
import com.li.chat.feign.admin.PermissionFeign;
import com.li.chat.param.admin.PermissionCreateParam;
import com.li.chat.param.admin.PermissionUpdateParam;
import com.li.chat.service.RouterService;
import com.li.chat.vo.router.RouterVO;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@Api(tags = "权限接口")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionFeign permissionFeign;
    private final RouterService routerService;

    public PermissionController(PermissionFeign permissionFeign, RouterService routerService) {
        this.permissionFeign = permissionFeign;
        this.routerService = routerService;
    }

    @ApiOperation("创建权限")
    @PostMapping("/create")
    @GlobalTransactional
    @RequiresPermission(value = "system:permission:add", message = "没有创建权限的权限")
    public ResultData create(@Valid @RequestBody PermissionCreateParam param) {
        PermissionDTO permissionDTO = new PermissionDTO();
        BeanUtils.copyProperties(param, permissionDTO);
        permissionDTO.setStatus(true);

        Long permissionId = permissionFeign.save(permissionDTO);

        return ResultData.success().put("permissionId", permissionId);
    }

    @ApiOperation("更新权限")
    @PutMapping("/update")
    @GlobalTransactional
    @RequiresPermission(value = "system:permission:update", message = "没有更新权限的权限")
    public ResultData update(@Valid @RequestBody PermissionUpdateParam param) {
        Long permissionId = param.getId();
        PermissionDTO permissionDTO = permissionFeign.getById(permissionId);
        if (permissionDTO == null) {
            return ResultData.error("权限不存在");
        }

        // 更新基本信息
        BeanUtils.copyProperties(param, permissionDTO);
        permissionFeign.save(permissionDTO);

        return ResultData.success();
    }

    @ApiOperation("获取权限列表")
    @GetMapping("/list")
    @RequiresPermission(value = "system:permission:list", message = "没有获取权限列表的权限")
    public ResultData list(@RequestParam(required = false) String name, PageParam pageParam) {
        PageResultData<PermissionDTO> pageResultData = permissionFeign.search(name != null ? name : "", pageParam);

        return ResultData.success(pageResultData);
    }

    @ApiOperation("获取树形权限")
    @GetMapping("/tree")
    @RequiresPermission(value = "system:permission:tree", message = "没有获取权限树形的权限")
    public ResultData tree(String name) {
        List<PermissionDTO> permissions = permissionFeign.list();

        // 构建树形结构
        List<PermissionDTO> tree = buildPermissionTree(permissions);

        return ResultData.success(tree);
    }

    @ApiOperation("获取权限详情")
    @GetMapping("/info/{id}")
    @RequiresPermission(value = "system:permission:info", message = "没有获取权限详情的权限")
    public ResultData info(@PathVariable("id") Long id) {
        PermissionDTO permissionDTO = permissionFeign.getById(id);
        if (permissionDTO == null) {
            return ResultData.error("权限不存在");
        }

        return ResultData.success(permissionDTO);
    }

    @ApiOperation("删除权限")
    @DeleteMapping("/delete/{id}")
    @GlobalTransactional
    @RequiresPermission(value = "system:permission:delete", message = "没有删除权限详情的权限")
    public ResultData delete(@PathVariable("id") Long id) {
        // 先检查是否有子权限
        List<PermissionDTO> children = permissionFeign.getByParentId(id);
        if (children != null && !children.isEmpty()) {
            return ResultData.error("该权限下存在子权限，无法删除");
        }

        permissionFeign.delete(id);
        return ResultData.success();
    }

    @ApiOperation("获取当前用户的路由")
    @GetMapping("/routes")
    public ResultData getUserRoutes() {
        Long adminId = RequestContext.getUserId();

        // 获取用户的权限
        Set<String> permissionCodes = permissionFeign.getUserPermissionCodes(adminId);

        // 获取所有菜单类型权限
        List<PermissionDTO> allMenus = permissionFeign.getAllMenuPermissions();

        // 过滤用户有权限的菜单
        List<PermissionDTO> userMenus = allMenus.stream()
                .filter(menu -> hasPermission(permissionCodes, menu))
                .collect(Collectors.toList());

        // 构建路由
        List<RouterVO> routes = routerService.buildRouters(userMenus);

        return ResultData.success(routes);
    }

    /**
     * 判断用户是否有权限访问某个菜单
     */
    private boolean hasPermission(Set<String> permissionCodes, PermissionDTO menu) {
        // 目录和菜单类型，判断用户是否有其下任一按钮的权限
        /*if (menu.getType() <= 1) {
            return true;
        }*/
        // 按钮类型，判断是否有对应权限码
        return permissionCodes.contains(menu.getCode());
    }

    /**
     * 构建权限树
     */
    private List<PermissionDTO> buildPermissionTree(List<PermissionDTO> permissions) {
        List<PermissionDTO> result = new ArrayList<>();

        // 按父ID分组
        Map<Long, List<PermissionDTO>> parentMap = permissions.stream()
                .collect(Collectors.groupingBy(PermissionDTO::getParentId));

        // 获取根权限
        List<PermissionDTO> roots = parentMap.getOrDefault(0L, new ArrayList<>());

        // 排序
        roots.sort(Comparator.comparing(PermissionDTO::getSort));

        // 递归构建树
        for (PermissionDTO root : roots) {
            buildChildren(root, parentMap);
            result.add(root);
        }

        return result;
    }

    private void buildChildren(PermissionDTO parent, Map<Long, List<PermissionDTO>> parentMap) {
        List<PermissionDTO> children = parentMap.getOrDefault(parent.getId(), new ArrayList<>());

        // 排序
        children.sort(Comparator.comparing(PermissionDTO::getSort));

        // 设置子权限
        parent.setChildren(children);

        // 递归处理子权限
        for (PermissionDTO child : children) {
            buildChildren(child, parentMap);
        }
    }
}
