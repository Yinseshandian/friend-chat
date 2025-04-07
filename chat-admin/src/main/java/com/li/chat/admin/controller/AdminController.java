package com.li.chat.admin.controller;

import com.li.chat.admin.converter.AdminConverter;
import com.li.chat.admin.entity.Admin;
import com.li.chat.admin.service.AdminService;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.admin.AdminDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@RestController
@RequestMapping("/chat-admin/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminConverter adminConverter;

    public AdminController(AdminService adminService, AdminConverter adminConverter) {
        this.adminService = adminService;
        this.adminConverter = adminConverter;
    }

    @PostMapping("/create")
    public Long create(@RequestBody AdminDTO adminDTO) {
        Admin admin = adminConverter.toEntity(adminDTO);
        adminService.save(admin);
        return admin.getId();
    }

    @PutMapping("/update")
    public void update(@RequestBody AdminDTO adminDTO) {
        Admin admin = adminConverter.toEntity(adminDTO);
        adminService.update(admin);
    }

    @GetMapping("/getById")
    public AdminDTO getById(@RequestParam("id") Long id) {
        Admin admin = adminService.findById(id);
        if (admin == null) {
            return null;
        }
        AdminDTO adminDTO = adminConverter.toDto(admin);
        return adminDTO;
    }

    @GetMapping("/getByUsername")
    public AdminDTO getByUsername(@RequestParam("username") String username) {
        Admin admin = adminService.findByUsername(username);
        if (admin == null) {
            return null;
        }
        AdminDTO adminDTO = adminConverter.toDto(admin);
        return adminDTO;
    }

    @GetMapping("/search")
    public PageResultData<AdminDTO> search(@SpringQueryMap AdminDTO adminDTO,
                                    @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        PageParam pageParam = PageParam.builder().pageNum(pageNum).pageSize(pageSize).build();
        Page<Admin> adminPage = adminService.findByAdminDTO(adminDTO, pageParam);
        List<AdminDTO> adminDTOList = adminPage.stream().map(adminConverter::toDto).collect(Collectors.toList());

        return PageResultData.<AdminDTO>builder()
                .total(adminPage.getTotalElements())
                .rows(adminDTOList)
                .pageSize(pageSize)
                .pageNum(pageNum).build();
    }

    @GetMapping("/list")
    public List<AdminDTO> list() {
        List<Admin> admins = adminService.findAll();
        return admins.stream().map(adminConverter::toDto).collect(Collectors.toList());
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam("id") Long id) {
        adminService.deleteById(id);
    }

    @PutMapping("/updatePassword")
    public void updatePassword(@RequestParam("id") Long id, @RequestParam("password") String password) {
        adminService.updatePassword(id, password);
    }

    @PutMapping("/updateStatus")
    public void updateStatus(@RequestParam("id") Long id, @RequestParam("status") Boolean status) {
        adminService.updateStatus(id, status);
    }

    @PostMapping("/assignRoles")
    public void assignRoles(@RequestParam("adminId") Long adminId, @RequestBody List<Long> roleIds) {
        adminService.assignRoles(adminId, roleIds);
    }


}