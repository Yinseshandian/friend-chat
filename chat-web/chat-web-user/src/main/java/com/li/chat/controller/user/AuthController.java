package com.li.chat.controller.user;

import cn.hutool.core.util.ObjectUtil;
import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.common.enums.UserLoginTypeEnum;
import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.domain.DTO.CaptchaDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.common.enums.UserEnum;
import com.li.chat.common.utils.IpUtils;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.common.utils.ResultData;
import com.li.chat.feign.CaptchaFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.param.user.AuthRegisterParam;
import com.li.chat.param.user.AuthLoginParam;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;

/**
 * @author malaka
 */
@Api(tags = "0101用户认证相关接口")
@RestController
@RequestMapping("/user/auth")
public class AuthController {

    private final CaptchaFeign captchaFeign;

    private final UserFeign userFeign;

    private final RedisCache redisCache;

    public AuthController(CaptchaFeign captchaFeign, UserFeign userFeign, RedisCache redisCache) {
        this.captchaFeign = captchaFeign;
        this.userFeign = userFeign;
        this.redisCache = redisCache;
    }

    /**
     * 获取验证码
     * @return
     */
    @ApiOperation(value = "图片验证码", notes = "不用鉴权，可直接访问")
    @GetMapping("/imgcaptcha")
    public ResultData imgCaptcha(HttpServletRequest request) {
        String ipAddr = IpUtils.getIpAddr(request);
        String key = "USER_REGISTER:" + ipAddr;
        System.out.println(key);
        CaptchaDTO captchaDTO = captchaFeign.imgCaptchaToBase64(key);
        return ResultData.success(captchaDTO);
    }

    @ApiOperation(value = "注册", notes = "不用鉴权，可直接访问")
    @GlobalTransactional
    @PostMapping("/register")
    public ResultData register(HttpServletRequest request,
                        @RequestBody @Valid AuthRegisterParam registerParam) {
        // 验证码
        String key =  "USER_REGISTER:" + IpUtils.getIpAddr(request);
        boolean captchaOk = captchaFeign.verifyRegister(key, registerParam.getCode());
        if (!captchaOk) {
            return ResultData.error(WebErrorCodeEnum.USER_AUTH_WRONG_CAPTCHA);
        }
        // 用户信息校验
        String username = registerParam.getUsername();
        UserDTO findUserDTO = userFeign.findByUsername(username);
        // 已创建用户
        if (ObjectUtil.isNotEmpty(findUserDTO)) {
            return ResultData.error(WebErrorCodeEnum.USER_AUTH_USER_ALREADY_EXISTS);
        }
        UserDTO addUserDto = new UserDTO();
        addUserDto.setSex(UserEnum.SEX_SECRET);
        addUserDto.setStatus(UserEnum.STATUS_OK);
        addUserDto.setUsername(username);
        addUserDto.setNickname(username);
        addUserDto.setPassword(registerParam.getPassword());
        Long userId = userFeign.add(addUserDto);

        return ResultData.success().put("userId", userId);
    }

    @ApiOperation(value = "登录", notes = "不用鉴权，可直接访问")
    @PostMapping("/login")
    public ResultData login(@RequestBody @Valid AuthLoginParam userLoginParam) {
        boolean isSusses = false;
        String username = userLoginParam.getUsername();
        // 账号密码登录
        if (UserLoginTypeEnum.PASSWORD.equals(userLoginParam.getType())) {

            isSusses = userFeign.checkUsernameAndPassword(username, userLoginParam.getPassword());

            if (!isSusses) {
                return ResultData.error(WebErrorCodeEnum.USER_AUTH_WRONG_INPUT_USER_OR_PASSWORD);
            }
        }

        if (isSusses) {
            UserDTO user = userFeign.findByUsername(username);
            String token = userFeign.login(user);
            HashMap<String, String> map = new HashMap<>();
            map.put("token", token);
            return ResultData.success(map);
        }
        return ResultData.error(WebErrorCodeEnum.USER_AUTH_LOGIN_FAIL);
    }

    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public ResultData logout(@RequestHeader("Authorization") String token) {
        redisCache.deleteObject(RedisCachePrefixEnum.USER_AUTH_LOGIN_TOKEN_TO_ID + token);
        return ResultData.success();
    }
}
