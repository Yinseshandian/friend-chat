package com.li.chat.feign.admin;

import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.admin.AdminDTO;
import com.li.chat.domain.admin.RoleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author malaka
 */

@FeignClient(name = "chat-admin", contextId="adminRole")
@RequestMapping("/chat-admin/role")
public interface RoleFeign {

    @PostMapping("/create")
    Long create(@RequestBody RoleDTO roleDTO);

    @PutMapping("/update")
    void update(@RequestBody RoleDTO roleDTO);


    @GetMapping("/getById")
    RoleDTO getById(@RequestParam("id") Long id);

    @GetMapping("/getByCode")
    RoleDTO getByCode(@RequestParam("code") String code);

    @GetMapping("/search")
    PageResultData<RoleDTO> search(@SpringQueryMap RoleDTO roleDTO,
                                   @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);

    @GetMapping("/list")
    List<RoleDTO> list();

    @DeleteMapping("/delete")
    void delete(@RequestParam("id") Long id);

    @PostMapping("/assignPermissions")
    void assignPermissions(@RequestParam("roleId") Long roleId, @RequestBody List<Long> permissionIds);
}