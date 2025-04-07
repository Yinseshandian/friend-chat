package com.li.chat.admin.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.li.chat.admin.entity.Admin;
import com.li.chat.admin.entity.Permission;
import com.li.chat.admin.entity.Role;
import com.li.chat.admin.repository.AdminRepository;
import com.li.chat.admin.repository.PermissionRepository;
import com.li.chat.admin.service.PermissionService;
import com.li.chat.common.param.PageParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final AdminRepository adminRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository, AdminRepository adminRepository) {
        this.permissionRepository = permissionRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission findById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    @Override
    public Permission findByCode(String code) {
        return permissionRepository.findByCode(code).orElse(null);
    }

    @Override
    public List<Permission> findByParentId(Long parentId) {
        return permissionRepository.findByParentIdOrderBySortAsc(parentId);
    }

    @Override
    public Page<Permission> findByNameContaining(String name, PageParam pageParam) {
        Pageable pageable = PageRequest.of(
                pageParam.getPageNum() - 1,
                pageParam.getPageSize(),
                Sort.by(Sort.Direction.ASC, "sort", "id")
        );
        return permissionRepository.findByNameContaining(name, pageable);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll(Sort.by(Sort.Direction.ASC, "sort", "id"));
    }

    @Override
    public List<Permission> findPermissionsByRoleIds(List<Long> roleIds) {
        List<Role> roles = roleIds.stream().map(v -> Role.builder().id(v).build()).collect(Collectors.toList());
        return permissionRepository.findAllByRolesIn(roles);
    }

    @Override
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public Set<String> getUserPermissionCodes(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElse(null);

        if (admin == null || admin.getRoles() == null || admin.getRoles().isEmpty()) {
            return Collections.emptySet();
        }

        // 获取角色ID列表
        /*List<Long> roleIds = admin.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());*/

        // 获取角色对应的权限
        List<Permission> permissions = permissionRepository.findAllByRolesIn(admin.getRoles());

        // 提取权限编码
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }


    @Override
    public List<Permission> findAllMenus() {
        // 获取类型为目录(0)和菜单(1)的权限
        return permissionRepository.findByTypeInAndStatusOrderBySortAsc(
                ListUtil.of(0, 1), true);
    }

}