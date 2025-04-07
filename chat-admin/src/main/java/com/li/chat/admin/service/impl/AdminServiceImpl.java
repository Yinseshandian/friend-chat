package com.li.chat.admin.service.impl;

/**
 * @author malaka
 */

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.digest.BCrypt;
import com.li.chat.admin.entity.Admin;
import com.li.chat.admin.entity.Role;
import com.li.chat.admin.repository.AdminRepository;
import com.li.chat.admin.repository.RoleRepository;
import com.li.chat.admin.service.AdminService;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.admin.AdminDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;

    public AdminServiceImpl(AdminRepository adminRepository, RoleRepository roleRepository) {
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public Admin findById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Page<Admin> findByUsernameContaining(String username, PageParam pageParam) {
        Pageable pageable = PageRequest.of(
                pageParam.getPageNum() - 1,
                pageParam.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );
        return adminRepository.findByUsernameContaining(username, pageable);
    }

    @Override
    public Page<Admin> findByAdminDTO(AdminDTO adminDTO, PageParam pageParam) {
        // 创建动态查询条件
        Specification<Admin> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 用户名模糊查询
            if (!StringUtils.isEmpty(adminDTO.getUsername())) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + adminDTO.getUsername() + "%"));
            }

            // 手机号模糊查询
            if (!StringUtils.isEmpty(adminDTO.getMobile())) {
                predicates.add(criteriaBuilder.like(root.get("mobile"), "%" + adminDTO.getMobile() + "%"));
            }

            // 邮箱模糊查询
            if (!StringUtils.isEmpty(adminDTO.getEmail())) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + adminDTO.getEmail() + "%"));
            }

            // 状态精确查询
            if (adminDTO.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), adminDTO.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 创建分页请求
        Pageable pageable = PageRequest.of(
                pageParam.getPageNum() - 1,
                pageParam.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );

        // 执行分页查询
        Page<Admin> adminPage = adminRepository.findAll(specification, pageable);

        return adminPage;
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public void updatePassword(Long id, String newPassword) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("管理员不存在"));
        admin.setPassword(BCrypt.hashpw(newPassword));
        adminRepository.save(admin);
    }

    @Override
    public void updateStatus(Long id, Boolean status) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("管理员不存在"));
        admin.setStatus(status);
        adminRepository.save(admin);
    }

    @Override
    public void assignRoles(Long adminId, List<Long> roleIds) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new RuntimeException("管理员不存在"));

        List<Role> roles = roleRepository.findAllById(roleIds);
        admin.setRoles(new HashSet<>(roles));

        adminRepository.save(admin);
    }

    @Override
    public void update(Admin admin) {
        if (ObjectUtils.isEmpty(admin.getId())) {
            throw  new RuntimeException("更新失败，未找到该管理员。");
        }
        Admin oldAdmin = adminRepository.findById(admin.getId()).orElse(null);
        if (ObjectUtils.isEmpty(oldAdmin)) {
            throw  new RuntimeException("更新失败，未找到该管理员。");
        }
        BeanUtil.copyProperties(admin, oldAdmin, CopyOptions.create().setIgnoreNullValue(true));
        adminRepository.save(oldAdmin);
    }
}