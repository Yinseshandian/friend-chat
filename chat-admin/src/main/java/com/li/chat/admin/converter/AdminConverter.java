package com.li.chat.admin.converter;

import com.li.chat.admin.entity.Admin;
import com.li.chat.common.utils.BeanCopyUtils;
import com.li.chat.domain.admin.AdminDTO;
import com.li.chat.domain.admin.RoleDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@Component
public class AdminConverter {

    private final RoleConverter roleConverter;

    public AdminConverter(RoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    /**
     * Admin实体转换为AdminDTO
     *
     * @param admin Admin实体
     * @return AdminDTO
     */
    public AdminDTO toDto(Admin admin) {
        if (admin == null) {
            return null;
        }

        // 复制基本属性
        AdminDTO adminDTO = BeanCopyUtils.copyBean(admin, AdminDTO.class);

        // 处理角色集合 - 调用RoleConverter来处理嵌套的Role转换
        if (admin.getRoles() != null && !admin.getRoles().isEmpty()) {
            List<RoleDTO> roleDTOs = admin.getRoles().stream()
                    .map(roleConverter::toDto)
                    .collect(Collectors.toList());
            adminDTO.setRoles(roleDTOs);
        }

        return adminDTO;
    }

    /**
     * AdminDTO转换为Admin实体
     *
     * @param adminDTO AdminDTO
     * @return Admin实体
     */
    public Admin toEntity(AdminDTO adminDTO) {
        if (adminDTO == null) {
            return null;
        }

        // 复制基本属性
        Admin admin = BeanCopyUtils.copyBean(adminDTO, Admin.class);

        // 处理角色集合 - 调用RoleConverter来处理嵌套的RoleDTO转换
        if (adminDTO.getRoles() != null && !adminDTO.getRoles().isEmpty()) {
            admin.setRoles(adminDTO.getRoles().stream()
                    .map(roleConverter::toEntity)
                    .collect(Collectors.toSet()));
        }

        return admin;
    }

    /**
     * Admin实体集合转换为AdminDTO集合
     *
     * @param adminList Admin实体集合
     * @return AdminDTO集合
     */
    public List<AdminDTO> toDtoList(List<Admin> adminList) {
        return BeanCopyUtils.copyListWithConvert(adminList, this::toDto);
    }

    /**
     * AdminDTO集合转换为Admin实体集合
     *
     * @param adminDTOList AdminDTO集合
     * @return Admin实体集合
     */
    public List<Admin> toEntityList(List<AdminDTO> adminDTOList) {
        return BeanCopyUtils.copyListWithConvert(adminDTOList, this::toEntity);
    }
}