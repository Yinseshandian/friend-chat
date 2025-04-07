package com.li.chat.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.li.chat.admin.entity.Admin;
import com.li.chat.admin.entity.Permission;
import com.li.chat.admin.entity.Role;
import com.li.chat.admin.repository.PermissionRepository;
import com.li.chat.admin.repository.RoleRepository;
import com.li.chat.admin.service.RoleService;
import com.li.chat.common.param.PageParam;
import com.li.chat.domain.admin.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author malaka
 */

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role findByCode(String code) {
        return roleRepository.findByCode(code).orElse(null);
    }

    @Override
    public Page<Role> findByNameContaining(String name, PageParam pageParam) {
        Pageable pageable = PageRequest.of(
                pageParam.getPageNum() - 1,
                pageParam.getPageSize(),
                Sort.by(Sort.Direction.ASC, "sort", "id")
        );
        return roleRepository.findByNameContaining(name, pageable);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "sort", "id"));
    }

    @Override
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("角色不存在"));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.setPermissions(new HashSet<>(permissions));

        roleRepository.save(role);
    }

    @Override
    public Page<Role> findByRoleDTO(RoleDTO roleDTO, PageParam pageParam) {
        // 创建动态查询条件
        Specification<Role> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!StringUtils.isEmpty(roleDTO.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + roleDTO.getName() + "%"));
            }

            if (!StringUtils.isEmpty(roleDTO.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + roleDTO.getCode() + "%"));
            }

            if (roleDTO.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), roleDTO.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 创建分页请求
        Pageable pageable = PageRequest.of(
                pageParam.getPageNum() - 1,
                pageParam.getPageSize(),
                Sort.by(Sort.Direction.ASC, "sort")
        );

        // 执行分页查询
        Page<Role> adminPage = roleRepository.findAll(specification, pageable);

        return adminPage;
    }

    @Override
    public  void update(Role role) {
        if (ObjectUtils.isEmpty(role.getId())) {
            throw  new RuntimeException("更新失败，未找到该管理员。");
        }
        Role oldRole = roleRepository.findById(role.getId()).orElse(null);
        if (ObjectUtils.isEmpty(oldRole)) {
            throw  new RuntimeException("更新失败，未找到该管理员。");
        }
        BeanUtil.copyProperties(role, oldRole, CopyOptions.create().setIgnoreNullValue(true));
        roleRepository.save(oldRole);
    }
}