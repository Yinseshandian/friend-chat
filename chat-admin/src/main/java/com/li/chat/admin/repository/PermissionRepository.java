package com.li.chat.admin.repository;

import com.li.chat.admin.entity.Permission;
import com.li.chat.admin.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
/**
 * @author malaka
 */


public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    Optional<Permission> findByCode(String code);

    List<Permission> findByParentIdOrderBySortAsc(Long parentId);

    Page<Permission> findByNameContaining(String name, Pageable pageable);

    // @Query("SELECT p FROM Permission p WHERE p.id IN (SELECT rp.permission.id FROM Role r JOIN r.permissions rp WHERE r.id IN :roleIds) AND p.status = true")
    // List<Permission> findPermissionsByRoleIds(List<Long> roleIds);

    List<Permission> findByTypeInAndStatusOrderBySortAsc(List<Integer> types, Boolean status);

    List<Permission> findAllByRolesIn(Collection<Role> roleIds);
}