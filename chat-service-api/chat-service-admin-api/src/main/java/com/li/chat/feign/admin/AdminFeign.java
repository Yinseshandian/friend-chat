package com.li.chat.feign.admin;

import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.admin.AdminDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author malaka
 */
@FeignClient(name = "chat-admin", contextId="admin")
@RequestMapping("/chat-admin/admin")
public interface AdminFeign {

    @PostMapping("/create")
    Long create(@RequestBody AdminDTO adminDTO);

    @PutMapping("/update")
    void update(@RequestBody AdminDTO adminDTO);

    @GetMapping("/getById")
    AdminDTO getById(@RequestParam("id") Long id);

    @GetMapping("/getByUsername")
    AdminDTO getByUsername(@RequestParam("username") String username);

    @GetMapping("/search")
    PageResultData<AdminDTO> search(@SpringQueryMap AdminDTO adminDTO,
                                    @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);

    @GetMapping("/list")
    List<AdminDTO> list();

    @DeleteMapping("/delete")
    void delete(@RequestParam("id") Long id);

    @PutMapping("/updatePassword")
    void updatePassword(@RequestParam("id") Long id, @RequestParam("password") String password);

    @PutMapping("/updateStatus")
    void updateStatus(@RequestParam("id") Long id, @RequestParam("status") Boolean status);

    @PostMapping("/assignRoles")
    void assignRoles(@RequestParam("adminId") Long adminId, @RequestBody List<Long> roleIds);

}
