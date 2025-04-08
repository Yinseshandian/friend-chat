package com.li.chat.feign;

import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.GroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author malaka
 */
@FeignClient(name = "chat-group", contextId = "groupManagement")
@RequestMapping("/chat-group/management")
public interface GroupManagementFeign {

    /**
     * 创建
     * @param groupDTO
     * @return
     */
    @PostMapping("/create")
    Long create(@RequestBody GroupDTO groupDTO);

    /**
     * id 查找群组
     * @param groupId
     * @return
     */
    @GetMapping("/findGroupById")
    GroupDTO findGroupById(@RequestParam("groupId") Long groupId);

    /**
     * 通过用户id查找群组
     * @param userId
     * @return
     */
    @GetMapping("/findAllGroupByUserId")
    List<GroupDTO> findAllGroupByUserId(@RequestParam("userId") Long userId);

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById")
    int deleteById(@RequestParam("id") Long id);

    /**
     * 更新
     * @param groupDTO
     */
    @PutMapping("/update")
    void update(@RequestBody GroupDTO groupDTO);

    /**
     * 名字模糊查询
     * @param name
     * @param pageParam
     * @return
     */
    @GetMapping("/findByName")
    PageResultData<GroupDTO> findByName(@RequestParam("name") String name, @SpringQueryMap PageParam pageParam);

    @GetMapping("/search")
    PageResultData<GroupDTO> search(@SpringQueryMap GroupDTO groupDTO, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize);
}
