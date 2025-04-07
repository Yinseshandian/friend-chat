package com.li.chat.admin.converter;

import com.li.chat.admin.entity.Permission;
import com.li.chat.common.utils.BeanCopyUtils;
import com.li.chat.domain.admin.PermissionDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author malaka
 */

@Component
public class PermissionConverter {

    /**
     * Permission实体转换为PermissionDTO
     *
     * @param permission Permission实体
     * @return PermissionDTO
     */
    public PermissionDTO toDto(Permission permission) {
        if (permission == null) {
            return null;
        }

        // 复制基本属性
        PermissionDTO permissionDTO = BeanCopyUtils.copyBean(permission, PermissionDTO.class);

        // 处理children集合（如果存在）
        // 注意: 这里假设PermissionDTO中有children字段
        // 如果没有该字段，可以移除这段代码
        if (permissionDTO.getChildren() == null) {
            permissionDTO.setChildren(new ArrayList<>());
        }

        return permissionDTO;
    }

    /**
     * PermissionDTO转换为Permission实体
     *
     * @param permissionDTO PermissionDTO
     * @return Permission实体
     */
    public Permission toEntity(PermissionDTO permissionDTO) {
        if (permissionDTO == null) {
            return null;
        }

        // 复制基本属性
        Permission permission = BeanCopyUtils.copyBean(permissionDTO, Permission.class);

        return permission;
    }

    /**
     * Permission实体集合转换为PermissionDTO集合
     *
     * @param permissionList Permission实体集合
     * @return PermissionDTO集合
     */
    public List<PermissionDTO> toDtoList(List<Permission> permissionList) {
        return BeanCopyUtils.copyListWithConvert(permissionList, this::toDto);
    }

    /**
     * PermissionDTO集合转换为Permission实体集合
     *
     * @param permissionDTOList PermissionDTO集合
     * @return Permission实体集合
     */
    public List<Permission> toEntityList(List<PermissionDTO> permissionDTOList) {
        return BeanCopyUtils.copyListWithConvert(permissionDTOList, this::toEntity);
    }
}