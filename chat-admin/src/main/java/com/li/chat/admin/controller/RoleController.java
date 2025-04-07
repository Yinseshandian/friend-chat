package com.li.chat.admin.controller;

import cn.hutool.json.JSONUtil;
import com.li.chat.admin.converter.RoleConverter;
import com.li.chat.admin.entity.Admin;
import com.li.chat.admin.entity.Role;
import com.li.chat.admin.service.RoleService;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.admin.AdminDTO;
import com.li.chat.domain.admin.RoleDTO;
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
@RequestMapping("/chat-admin/role")
public class RoleController{

    private final RoleService roleService;
    private final RoleConverter roleConverter;

    public RoleController(RoleService roleService, RoleConverter roleConverter) {
        this.roleService = roleService;
        this.roleConverter = roleConverter;
    }


    @PostMapping("/create")
    public Long create(@RequestBody RoleDTO roleDTO) {
        Role role = roleConverter.toEntity(roleDTO);
        roleService.save(role);
        return role.getId();
    }

    @PutMapping("/update")
    public void update(@RequestBody RoleDTO roleDTO) {
        Role role = roleConverter.toEntity(roleDTO);
        roleService.update(role);
    }


    @GetMapping("/getById")
    public RoleDTO getById(@RequestParam("id") Long id) {
        Role role = roleService.findById(id);
        if (role == null) {
            return null;
        }
        return roleConverter.toDto(role);
    }

    @GetMapping("/getByCode")
    public RoleDTO getByCode(@RequestParam("code") String code) {
        Role role = roleService.findByCode(code);
        if (role == null) {
            return null;
        }
        return roleConverter.toDto(role);
    }

    @GetMapping("/search")
    public PageResultData<RoleDTO> search(@SpringQueryMap RoleDTO roleDTO,
                                          @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        PageParam pageParam = PageParam.builder().pageNum(pageNum).pageSize(pageSize).build();
        Page<Role> rolePage = roleService.findByRoleDTO(roleDTO, pageParam);
        List<RoleDTO> adminDTOList = rolePage.stream().map(roleConverter::toDto).collect(Collectors.toList());

        return PageResultData.<RoleDTO>builder()
                .total(rolePage.getTotalElements())
                .rows(adminDTOList)
                .pageSize(pageParam.getPageSize())
                .pageNum(pageParam.getPageNum()).build();
    }

    @GetMapping("/list")
    public List<RoleDTO> list() {
        List<Role> roles = roleService.findAll();
        return roles.stream().map(roleConverter::toDto).collect(Collectors.toList());
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam("id") Long id) {
        roleService.deleteById(id);
    }

    @PostMapping("/assignPermissions")
    public void assignPermissions(@RequestParam("roleId") Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
    }

}