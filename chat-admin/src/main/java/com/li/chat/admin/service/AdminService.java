package com.li.chat.admin.service;

import com.li.chat.admin.entity.Admin;
import com.li.chat.common.param.PageParam;
import com.li.chat.domain.admin.AdminDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author malaka
 */
public interface AdminService {

    Admin save(Admin admin);

    Admin findById(Long id);

    Admin findByUsername(String username);

    Page<Admin> findByUsernameContaining(String username, PageParam pageParam);

    Page<Admin> findByAdminDTO(AdminDTO adminDTO, PageParam pageParam);

    List<Admin> findAll();

    void deleteById(Long id);

    void updatePassword(Long id, String newPassword);

    void updateStatus(Long id, Boolean status);

    void assignRoles(Long adminId, List<Long> roleIds);

    void update(Admin admin);
}
