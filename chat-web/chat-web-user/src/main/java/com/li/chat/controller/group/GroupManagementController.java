package com.li.chat.controller.group;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.li.chat.common.enums.GroupApplyEnum;
import com.li.chat.common.enums.GroupJoinModeEnum;
import com.li.chat.common.enums.GroupMemberTypeEnum;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.DefaultGroupParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.domain.DTO.GroupApplyDTO;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.param.group.GroupApplyParam;
import com.li.chat.param.group.GroupCreateParam;
import com.li.chat.param.group.GroupUpdateParam;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * @author malaka
 */
@Api(tags = "0201群组管理接口")
@RestController
@RequestMapping("/group/management")
public class GroupManagementController {

    private final GroupManagementFeign groupManagementFeign;
    private final GroupMemberFeign groupMemberFeign;


    public GroupManagementController(GroupManagementFeign groupManagementFeign, GroupMemberFeign groupMemberFeign) {
        this.groupManagementFeign = groupManagementFeign;
        this.groupMemberFeign = groupMemberFeign;
    }

    @ApiOperation(value = "创建群聊")
    @GlobalTransactional
    @PostMapping("/create")
    public ResultData create(@Valid @RequestBody GroupCreateParam param) {
        Long userId = RequestContext.getUserId();
        GroupDTO groupDTO = GroupDTO.builder()
                .name(param.getName())
                .joinMode(param.getJoinMode())
                .memberSize(DefaultGroupParam.DEFAULT_GROUP_MEMBER_SIZE)
                .memberNum(1)
                .holderUserId(userId)
                // 默认头像
                .photo("http://127.0.0.1:9000/friendchat/group-avatar/group.jpg")
                .introduction("")
                .build();
        Long groupId = groupManagementFeign.create(groupDTO);
        GroupMemberDTO member = GroupMemberDTO.builder()
                .userId(userId)
                .groupId(groupId)
                .type(GroupMemberTypeEnum.TYPE_MASTER)
                .build();
        groupMemberFeign.create(member);
        return ResultData.success().put("groupId", groupId);
    }

    @ApiOperation(value = "群聊列表")
    @GlobalTransactional
    @GetMapping("/list")
    public ResultData list() {
        Long userId = RequestContext.getUserId();
        List<GroupDTO> groupDTOList = groupManagementFeign.findAllGroupByUserId(userId);
        return ResultData.success(groupDTOList);
    }

    @ApiOperation(value = "群聊信息")
    @GlobalTransactional
    @GetMapping("/info/{groupId}")
    public ResultData info(@PathVariable("groupId") Long groupId) {
        Long userId = RequestContext.getUserId();
        if (!groupMemberFeign.isGroupMember(userId, groupId)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MEMBER);
        }
        GroupDTO group = groupManagementFeign.findGroupById(groupId);

        return ResultData.success(group);
    }

    @ApiOperation(value = "删除群聊")
    @GlobalTransactional
    @DeleteMapping("/delete/{groupId}")
    public ResultData delete(@PathVariable("groupId") Long groupId) {
        Long userId = RequestContext.getUserId();
        GroupMemberDTO manager = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        if (BeanUtil.isEmpty(manager)
                || ObjectUtil.notEqual(manager.getType(), GroupMemberTypeEnum.TYPE_MASTER)) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MASTER);
        }
        groupManagementFeign.deleteById(groupId);

        return ResultData.success();
    }

    @ApiOperation(value = "更新群聊")
    @GlobalTransactional
    @PutMapping("/update")
    public ResultData delete(@RequestBody GroupUpdateParam param) {
        Long userId = RequestContext.getUserId();
        Long groupId = param.getId();
        GroupMemberDTO manager = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        if (BeanUtil.isEmpty(manager)
                || (ObjectUtil.notEqual(manager.getType(), GroupMemberTypeEnum.TYPE_MASTER)
                && ObjectUtil.notEqual(manager.getType(), GroupMemberTypeEnum.TYPE_MANAGER))) {
            return ResultData.error(WebErrorCodeEnum.GROUP_MEMBER_NOT_MANAGER);
        }
        GroupDTO groupDTO = new GroupDTO();
        BeanUtils.copyProperties(param, groupDTO);
        groupManagementFeign.update(groupDTO);

        return ResultData.success();
    }

    @ApiOperation(value = "名字查询群组")
    @GetMapping("/search")
    public PageResultData myGroupApply(@RequestParam("s") String s, PageParam param) {
        Long userId = RequestContext.getUserId();
        PageResultData<GroupDTO> pageResultData = groupManagementFeign.findByName(s, param);
        pageResultData.setCode(HttpStatus.OK.value());
        return pageResultData;
    }

}
