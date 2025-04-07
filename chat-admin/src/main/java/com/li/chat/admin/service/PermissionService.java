package com.li.chat.admin.service;

import com.li.chat.admin.entity.Permission;
import com.li.chat.common.param.PageParam;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

/**
 * @author malaka
 */
public interface PermissionService {
    Permission save(Permission permission);

    Permission findById(Long id);

    Permission findByCode(String code);

    List<Permission> findByParentId(Long parentId);

    Page<Permission> findByNameContaining(String name, PageParam pageParam);

    List<Permission> findAll();

    List<Permission> findPermissionsByRoleIds(List<Long> roleIds);

    void deleteById(Long id);

    Set<String> getUserPermissionCodes(Long adminId);

    List<Permission> findAllMenus();

}
