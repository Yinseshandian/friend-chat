package com.li.chat.feign.admin;

import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.domain.admin.RoleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author malaka
 */
@FeignClient(name = "chat-admin", contextId="adminPermission")
@RequestMapping("/chat-admin/permission")
public interface PermissionFeign {

    @PostMapping("/save")
    Long save(@RequestBody PermissionDTO permissionDTO);

    @GetMapping("/getById")
    PermissionDTO getById(@RequestParam("id") Long id);

    @GetMapping("/getByCode")
    PermissionDTO getByCode(@RequestParam("code") String code);

    @GetMapping("/getByParentId")
    List<PermissionDTO> getByParentId(@RequestParam("parentId") Long parentId);

    @GetMapping("/search")
    PageResultData<PermissionDTO> search(@RequestParam("name") String name, @SpringQueryMap PageParam pageParam);

    @GetMapping("/list")
    List<PermissionDTO> list();

    @GetMapping("/getByRoleIds")
    List<PermissionDTO> getByRoleIds(@RequestParam("roleIds") List<Long> roleIds);

    @DeleteMapping("/delete")
    void delete(@RequestParam("id") Long id);

    @GetMapping("/getUserPermissionCodes")
    Set<String> getUserPermissionCodes(@RequestParam("adminId") Long adminId);

    @GetMapping("/getAllMenuPermissions")
    List<PermissionDTO> getAllMenuPermissions();
}