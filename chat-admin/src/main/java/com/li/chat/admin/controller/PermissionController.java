package com.li.chat.admin.controller;

import com.li.chat.admin.converter.PermissionConverter;
import com.li.chat.admin.entity.Permission;
import com.li.chat.admin.service.PermissionService;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.domain.admin.RoleDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@RestController
@RequestMapping("/chat-admin/permission")
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionConverter permissionConverter;

    public PermissionController(PermissionService permissionService, PermissionConverter permissionConverter) {
        this.permissionService = permissionService;
        this.permissionConverter = permissionConverter;
    }

    @PostMapping("/save")
    public Long save(@RequestBody PermissionDTO permissionDTO) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO, permission);
        permissionService.save(permission);
        return permission.getId();
    }

    @GetMapping("/getById")
    public PermissionDTO getById(@RequestParam("id") Long id) {
        Permission permission = permissionService.findById(id);
        if (permission == null) {
            return null;
        }
        PermissionDTO permissionDTO = new PermissionDTO();
        BeanUtils.copyProperties(permission, permissionDTO);
        return permissionDTO;
    }

    @GetMapping("/getByCode")
    public PermissionDTO getByCode(@RequestParam("code") String code) {
        Permission permission = permissionService.findByCode(code);
        if (permission == null) {
            return null;
        }
        PermissionDTO permissionDTO = new PermissionDTO();
        BeanUtils.copyProperties(permission, permissionDTO);
        return permissionDTO;
    }

    @GetMapping("/getByParentId")
    public List<PermissionDTO> getByParentId(@RequestParam("parentId") Long parentId) {
        List<Permission> permissions = permissionService.findByParentId(parentId);
        return permissions.stream().map(permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            BeanUtils.copyProperties(permission, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public PageResultData<PermissionDTO> search(@RequestParam("name") String name, @SpringQueryMap PageParam pageParam) {
        Page<Permission> permissionPage = permissionService.findByNameContaining(name, pageParam);
        List<PermissionDTO> permissionDTOList = permissionPage.stream().map(permissionConverter::toDto).collect(Collectors.toList());

        return PageResultData.<PermissionDTO>builder()
                .total(permissionPage.getTotalElements())
                .rows(permissionDTOList)
                .pageSize(pageParam.getPageSize())
                .pageNum(pageParam.getPageNum()).build();
    }

    @GetMapping("/list")
    public List<PermissionDTO> list() {
        List<Permission> permissions = permissionService.findAll();
        return permissions.stream().map(permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            BeanUtils.copyProperties(permission, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());
    }

    @GetMapping("/getByRoleIds")
    public List<PermissionDTO> getByRoleIds(@RequestParam("roleIds") List<Long> roleIds) {
        List<Permission> permissions = permissionService.findPermissionsByRoleIds(roleIds);
        return permissions.stream().map(permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            BeanUtils.copyProperties(permission, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam("id") Long id) {
        permissionService.deleteById(id);
    }

    @GetMapping("/getUserPermissionCodes")
    public Set<String> getUserPermissionCodes(@RequestParam("adminId") Long adminId) {
        return permissionService.getUserPermissionCodes(adminId);
    }

    @GetMapping("/getAllMenuPermissions")
    public List<PermissionDTO> getAllMenuPermissions() {
        List<Permission> permissions = permissionService.findAllMenus();
        return permissions.stream().map(permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            BeanUtils.copyProperties(permission, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());
    }
}