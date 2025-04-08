package com.li.chat.controller.group;

import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.enums.GroupMemberTypeEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.feign.FileFeign;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.vo.group.GroupMemberVo;
import com.li.chat.vo.group.GroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@Api(tags = "群组管理接口")
@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupManagementFeign groupManagementFeign;
    private final GroupMemberFeign groupMemberFeign;
    private final UserFeign userFeign;
    private final FileFeign fileFeign;

    public GroupController(GroupManagementFeign groupManagementFeign, GroupMemberFeign groupMemberFeign,
                           UserFeign userFeign, FileFeign fileFeign) {
        this.groupManagementFeign = groupManagementFeign;
        this.groupMemberFeign = groupMemberFeign;
        this.userFeign = userFeign;
        this.fileFeign = fileFeign;
    }

    @ApiOperation("分页查询群组列表")
    @GetMapping("/list")
    @RequiresPermission(value = "group:group:list", message = "没有查询群组列表的权限")
    public ResultData list(
            GroupDTO groupDTO,
            PageParam pageParam) {

        // 构建分页结果
        PageResultData<GroupDTO> pageData = groupManagementFeign.search(groupDTO, pageParam.getPageNum(), pageParam.getPageSize());

        // 查询群主信息并组装VO
        List<GroupVO> voList = new ArrayList<>();
        Set<Long> userIds = new HashSet<>();

        // 收集所有用户ID
        for (GroupDTO group : pageData.getRows()) {
            if (group.getHolderUserId() != null) {
                userIds.add(group.getHolderUserId());
            }
        }

        // 批量查询用户信息
        Map<Long, UserDTO> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserDTO> userList = userFeign.findAllUnDelByIds(userIds);
            userMap = userList.stream()
                    .collect(Collectors.toMap(UserDTO::getId, user -> user));
        }

        // 组装VO
        for (GroupDTO group : pageData.getRows()) {
            GroupVO vo = GroupVO.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .photo(group.getPhoto())
                    .introduction(group.getIntroduction())
                    .memberSize(group.getMemberSize())
                    .memberNum(group.getMemberNum())
                    .holderUserId(group.getHolderUserId())
                    .joinMode(group.getJoinMode())
                    .createTime(group.getCreateTime())
                    .build();

            // 设置群主信息
            if (group.getHolderUserId() != null) {
                UserDTO holder = userMap.get(group.getHolderUserId());
                if (holder != null) {
                    vo.setHolderUsername(holder.getUsername());
                    vo.setHolderAvatar(holder.getAvatar());
                }
            }

            voList.add(vo);
        }

        // 构建最终结果
        PageResultData<GroupVO> result = new PageResultData<>();
        result.setTotal(pageData.getTotal());
        result.setRows(voList);
        result.setPageNum(pageParam.getPageNum());
        result.setPageSize(pageParam.getPageSize());

        return ResultData.success(result);
    }

    @ApiOperation("获取群组详情")
    @GetMapping("/info/{id}")
    @RequiresPermission(value = "group:group:info", message = "没有查看群组详情的权限")
    public ResultData getInfo(@PathVariable("id") Long id) {
        // 查询群组信息
        GroupDTO group = groupManagementFeign.findGroupById(id);
        if (group == null) {
            return ResultData.error("群组不存在");
        }

        // 组装VO
        GroupVO vo = GroupVO.builder()
                .id(group.getId())
                .name(group.getName())
                .photo(group.getPhoto())
                .introduction(group.getIntroduction())
                .memberSize(group.getMemberSize())
                .memberNum(group.getMemberNum())
                .holderUserId(group.getHolderUserId())
                .joinMode(group.getJoinMode())
                .createTime(group.getCreateTime())
                .build();

        // 设置群主信息
        if (group.getHolderUserId() != null) {
            UserDTO holder = userFeign.findUserById(group.getHolderUserId());
            if (holder != null) {
                vo.setHolderUsername(holder.getUsername());
                vo.setHolderAvatar(holder.getAvatar());
            }
        }

        return ResultData.success(vo);
    }

    @ApiOperation("创建群组")
    @PostMapping("/create")
    @RequiresPermission(value = "group:group:add", message = "没有创建群组的权限")
    public ResultData create(@Valid @RequestBody GroupDTO groupDTO) {
        // 检查群主是否存在
        if (groupDTO.getHolderUserId() != null) {
            UserDTO holder = userFeign.findUserById(groupDTO.getHolderUserId());
            if (holder == null) {
                return ResultData.error("群主用户不存在");
            }
        }

        // 创建群组
        Long groupId = groupManagementFeign.create(groupDTO);

        // 添加群主为群成员
        if (groupDTO.getHolderUserId() != null) {
            GroupMemberDTO memberDTO = GroupMemberDTO.builder()
                    .userId(groupDTO.getHolderUserId())
                    .groupId(groupId)
                    .type(GroupMemberTypeEnum.TYPE_MASTER)
                    .build();
            groupMemberFeign.create(memberDTO);
        }

        return ResultData.success().put("groupId", groupId);
    }

    @ApiOperation("更新群组信息")
    @PutMapping("/update")
    @RequiresPermission(value = "group:group:edit", message = "没有编辑群组的权限")
    public ResultData update(@Valid @RequestBody GroupDTO groupDTO) {
        // 检查群组是否存在
        GroupDTO existGroup = groupManagementFeign.findGroupById(groupDTO.getId());
        if (existGroup == null) {
            return ResultData.error("群组不存在");
        }

        // 检查群主是否存在
        if (groupDTO.getHolderUserId() != null && !groupDTO.getHolderUserId().equals(existGroup.getHolderUserId())) {
            UserDTO holder = userFeign.findUserById(groupDTO.getHolderUserId());
            if (holder == null) {
                return ResultData.error("群主用户不存在");
            }

            // 检查新群主是否为群成员
            boolean isMember = groupMemberFeign.isGroupMember(groupDTO.getHolderUserId(), groupDTO.getId());
            if (!isMember) {
                return ResultData.error("新群主不是群成员，无法转让");
            }

            // 更新原群主和新群主的成员类型
            GroupMemberDTO oldHolderMember = groupMemberFeign.findByGroupIdAndUserId(groupDTO.getId(), existGroup.getHolderUserId());
            if (oldHolderMember != null) {
                oldHolderMember.setType(0); // 降为普通成员
                groupMemberFeign.update(oldHolderMember);
            }

            GroupMemberDTO newHolderMember = groupMemberFeign.findByGroupIdAndUserId(groupDTO.getId(), groupDTO.getHolderUserId());
            if (newHolderMember != null) {
                newHolderMember.setType(2); // 设为群主
                groupMemberFeign.update(newHolderMember);
            }
        }

        // 更新群组信息
        groupManagementFeign.update(groupDTO);

        return ResultData.success();
    }

    @ApiOperation("删除群组")
    @DeleteMapping("/delete/{id}")
    @RequiresPermission(value = "group:group:delete", message = "没有删除群组的权限")
    public ResultData delete(@PathVariable("id") Long id) {
        // 检查群组是否存在
        GroupDTO group = groupManagementFeign.findGroupById(id);
        if (group == null) {
            return ResultData.error("群组不存在");
        }

        // 删除群组
        int result = groupManagementFeign.deleteById(id);
        if (result > 0) {
            return ResultData.success();
        } else {
            return ResultData.error("删除失败");
        }
    }

    @ApiOperation("获取群成员列表")
    @GetMapping("/members/{groupId}")
    @RequiresPermission(value = "group:member:list", message = "没有查看群组详情的权限")
    public ResultData getMembers(@PathVariable("groupId") Long groupId) {
        // 检查群组是否存在
        GroupDTO group = groupManagementFeign.findGroupById(groupId);
        if (group == null) {
            return ResultData.error("群组不存在");
        }

        // 查询群成员
        List<GroupMemberDTO> members = groupMemberFeign.findAllByGroupId(groupId);
        if (members.isEmpty()) {
            return ResultData.success(new ArrayList<>());
        }

        // 获取所有用户ID
        Set<Long> userIds = members.stream()
                .map(GroupMemberDTO::getUserId)
                .collect(Collectors.toSet());

        // 批量查询用户信息
        Map<Long, UserDTO> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserDTO> userList = userFeign.findAllUnDelByIds(userIds);
            userMap = userList.stream()
                    .collect(Collectors.toMap(UserDTO::getId, user -> user));
        }

        // 组装VO
        List<GroupMemberVo> voList = new ArrayList<>();
        for (GroupMemberDTO member : members) {
            UserDTO user = userMap.get(member.getUserId());
            if (user != null) {
                GroupMemberVo vo = GroupMemberVo.builder()
                        .id(member.getId())
                        .userId(member.getUserId())
                        .remark(member.getNickname())
                        .type(member.getType())
                        .username(user.getUsername())
                        .avatar(user.getAvatar())
                        .build();
                voList.add(vo);
            }
        }

        return ResultData.success(voList);
    }

    @ApiOperation("上传群组头像")
    @PostMapping("/upload/avatar")
    @RequiresPermission(value = "group:group:edit", message = "没有编辑群组的权限")
    public ResultData uploadAvatar(@RequestParam("file") MultipartFile file) {
        String type = file.getContentType();
        if (type == null || !type.startsWith("image/")) {
            return ResultData.error("只能上传图片文件");
        }

        try {
            String url = fileFeign.upload(file, "group-avatar");
            return ResultData.success().put("url", url);
        } catch (Exception e) {
            return ResultData.error("上传失败: " + e.getMessage());
        }
    }
}