package com.li.chat.admin.service;

import com.li.chat.admin.entity.Role;
import com.li.chat.common.param.PageParam;
import com.li.chat.domain.admin.RoleDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author malaka
 */
public interface RoleService {
    Role save(Role role);

    Role findById(Long id);

    Role findByCode(String code);

    Page<Role> findByNameContaining(String name, PageParam pageParam);

    List<Role> findAll();

    void deleteById(Long id);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    Page<Role> findByRoleDTO(RoleDTO roleDTO, PageParam pageParam);

    void update(Role role);
}
