package com.li.chat.controller.user;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.Objects;
import com.li.chat.annotation.MyApiResult;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.common.utils.CheckImagesFormatUtil;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.common.utils.ResultData;
import com.li.chat.feign.FileFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.param.user.UserUpdateParam;
import com.li.chat.param.user.UserUpdatePwdParam;
import com.li.chat.vo.TestResult;
import com.li.chat.vo.UserInfoVo;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author malaka
 */
@Api(tags = "0102用户信息相关接口")
@Slf4j
@RestController
@RequestMapping("/user/info")
public class UserInfoController {

    private final UserFeign userFeign;

    private final FileFeign fileFeign;

    public UserInfoController(UserFeign userFeign, FileFeign fileFeign) {
        this.userFeign = userFeign;
        this.fileFeign = fileFeign;
    }

    /**
     * 我的个人信息
     * @return
     */
    @ApiOperation(value = "个人信息")
    @GetMapping("/profile")
    public ResultData<UserInfoVo> profile() {
        Long userId = RequestContext.getUserId();
        UserDTO userDTO = userFeign.findUserById(userId);
        // 查找失败
        if (ObjectUtils.isEmpty(userDTO)) {
            return ResultData.error(WebErrorCodeEnum.SERVICE_ERROR);
        }
        return ResultData.success(UserInfoVo.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .avatar(userDTO.getAvatar())
                .nickname(userDTO.getNickname())
                .signature(userDTO.getSignature())
                .sex(userDTO.getSex())
                .status(userDTO.getStatus())
                .build()
        );
    }

    /**
     * 通过id查询用户信息
     * @return
     */
    @ApiOperation("通过id查询用户信息")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "userId", value = "用户id")
    )
    @GetMapping("/{userId}")
    public ResultData infoById(@PathVariable("userId") Long userId) {
        UserDTO userDTO = userFeign.findUserById(userId);
        // 查找失败
        if (ObjectUtils.isEmpty(userDTO)) {
            return ResultData.error(WebErrorCodeEnum.USER_INFO_USER_NOT_FOUND);
        }
        return ResultData.success(UserInfoVo.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .avatar(userDTO.getAvatar())
                .nickname(userDTO.getNickname())
                .signature(userDTO.getSignature())
                .sex(userDTO.getSex())
                .build()
        );
    }

    @ApiOperation(value = "用户名查询用户")
    @GetMapping("/search")
    public ResultData search(@RequestParam("username") String username) {
        UserDTO userDTO = userFeign.findByUsername(username);
        Long userId = RequestContext.getUserId();
        // 移除当前用户
        if (ObjectUtil.equal(userId, userDTO)) {
            return ResultData.success();
        }
        return ResultData.success(userDTO);
    }


    @ApiOperation("更改用户信息")
    @GlobalTransactional
    @PutMapping("update")
    public ResultData update(@RequestBody @Valid UserUpdateParam userUpdateParam) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userUpdateParam, userDTO);
        Long userId = RequestContext.getUserId();
        userDTO.setId(userId);
        userFeign.update(userDTO);
        return ResultData.success();
    }

    @ApiOperation("更改用户密码")
    @GlobalTransactional
    @PutMapping("/password")
    public ResultData updatePassword(@RequestBody @Valid UserUpdatePwdParam updatePwdParam) {
        UserDTO userDTO = new UserDTO();
        Long userId = RequestContext.getUserId();
        userDTO.setId(userId);
        userDTO.setPassword(updatePwdParam.getOldPassword());
        // 旧密码错误
        if (!userFeign.checkPassword(userDTO)) {
            return ResultData.error(WebErrorCodeEnum.USER_INFO_OLD_PASSWORD_WRONG);
        }
        userDTO.setPassword(updatePwdParam.getNewPassword());
        userFeign.update(userDTO);
        return ResultData.success();
    }

    @ApiOperation("上传用户头像")
    @GlobalTransactional
    @PostMapping(value = "/avatar/upload")
    public ResultData uploadAvatar(@RequestPart("file") MultipartFile file) {
        String type = file.getContentType();
        type = type.substring(0, type.indexOf("/"));
        if (!Objects.equal("image",type)) {
            return ResultData.error(WebErrorCodeEnum.USER_INFO_AVATAR_FILE_TYPE_WRONG);
        }
        try {
            if (!CheckImagesFormatUtil.checkImageElement(file.getInputStream(), 100, 100)) {
                // return ResultData.error(WebErrorCodeEnum.USER_INFO_AVATAR_FILE_SIZE_WRONG);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String originalFilename = file.getOriginalFilename();
        long size = file.getSize();
        log.info("user-avatar-用户 {} 上传文件：{} 大小:{}",RequestContext.getUserId(), originalFilename, size);
        String url = fileFeign.upload(file, "user-avatar");
        return ResultData.success().put("url", url);
    }

    @ApiOperation("tsts")
    @MyApiResult(UserInfoVo.class)
    @GetMapping("/test")
    public TestResult<UserInfoVo> test() {
        return null;
    }


}
