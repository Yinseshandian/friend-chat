package com.li.chat.admin.converter;

import com.li.chat.admin.entity.Role;
import com.li.chat.common.utils.BeanCopyUtils;
import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.domain.admin.RoleDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@Component
public class RoleConverter {

    private final PermissionConverter permissionConverter;

    public RoleConverter(PermissionConverter permissionConverter) {
        this.permissionConverter = permissionConverter;
    }

    /**
     * Role实体转换为RoleDTO
     *
     * @param role Role实体
     * @return RoleDTO
     */
    public RoleDTO toDto(Role role) {
        if (role == null) {
            return null;
        }

        // 复制基本属性
        RoleDTO roleDTO = BeanCopyUtils.copyBean(role, RoleDTO.class);

        // 处理权限集合 - 调用PermissionConverter处理嵌套的Permission转换
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            List<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                    .map(permissionConverter::toDto)
                    .collect(Collectors.toList());
            roleDTO.setPermissions(permissionDTOs);
        }

        return roleDTO;
    }

    /**
     * RoleDTO转换为Role实体
     *
     * @param roleDTO RoleDTO
     * @return Role实体
     */
    public Role toEntity(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }

        // 复制基本属性
        Role role = BeanCopyUtils.copyBean(roleDTO, Role.class);

        // 处理权限集合 - 调用PermissionConverter处理嵌套的PermissionDTO转换
        if (roleDTO.getPermissions() != null && !roleDTO.getPermissions().isEmpty()) {
            role.setPermissions(roleDTO.getPermissions().stream()
                    .map(permissionConverter::toEntity)
                    .collect(Collectors.toSet()));
        }

        return role;
    }

    /**
     * Role实体集合转换为RoleDTO集合
     *
     * @param roleList Role实体集合
     * @return RoleDTO集合
     */
    public List<RoleDTO> toDtoList(List<Role> roleList) {
        return BeanCopyUtils.copyListWithConvert(roleList, this::toDto);
    }

    /**
     * RoleDTO集合转换为Role实体集合
     *
     * @param roleDTOList RoleDTO集合
     * @return Role实体集合
     */
    public List<Role> toEntityList(List<RoleDTO> roleDTOList) {
        return BeanCopyUtils.copyListWithConvert(roleDTOList, this::toEntity);
    }
}