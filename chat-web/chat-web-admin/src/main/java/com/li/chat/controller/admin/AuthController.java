package com.li.chat.controller.admin;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.common.utils.ResultData;
import com.li.chat.config.TokenProperties;
import com.li.chat.domain.DTO.CaptchaDTO;
import com.li.chat.domain.admin.AdminDTO;
import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.feign.CaptchaFeign;
import com.li.chat.feign.admin.AdminFeign;
import com.li.chat.feign.admin.PermissionFeign;
import com.li.chat.param.admin.LoginParam;
import com.li.chat.vo.admin.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@Api(tags = "认证接口")
@RestController
@RequestMapping("/admin")
public class AuthController {

    private final AdminFeign adminFeign;
    private final PermissionFeign permissionFeign;
    private final StringRedisTemplate redisTemplate;
    private final CaptchaFeign captchaFeign;
    private final TokenProperties tokenProperties;

    public AuthController(AdminFeign adminFeign, PermissionFeign permissionFeign, StringRedisTemplate redisTemplate, CaptchaFeign captchaFeign, TokenProperties tokenProperties) {
        this.adminFeign = adminFeign;
        this.permissionFeign = permissionFeign;
        this.redisTemplate = redisTemplate;
        this.captchaFeign = captchaFeign;
        this.tokenProperties = tokenProperties;
    }

    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public ResultData captcha() {
        // 生成验证码
        String key = UUID.randomUUID().toString(true);
        CaptchaDTO captchaDTO = captchaFeign.imgCaptchaToBase64(key);

        return ResultData.success()
                .put("captchaId", key)
                .put("captchaImg", captchaDTO.getBase64Img());
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public ResultData login(@Valid @RequestBody LoginParam param) {
        // 验证验证码
        if (!captchaFeign.verifyRegister( param.getCaptchaId(), param.getCaptchaCode())) {
            return ResultData.error("验证码错误");
        }

        // 查询管理员
        AdminDTO admin = adminFeign.getByUsername(param.getUsername());
        if (admin == null) {
            return ResultData.error("用户名或密码错误");
        }

        // 验证密码
        if (!BCrypt.checkpw(param.getPassword(), admin.getPassword())) {
            return ResultData.error("用户名或密码错误");
        }

        // 验证状态
        if (!Boolean.TRUE.equals(admin.getStatus())) {
            return ResultData.error("账号已被禁用");
        }

        // 更新最后登录时间
        AdminDTO updateAdmin = new AdminDTO();
        updateAdmin.setId(admin.getId());
        updateAdmin.setLastLoginTime(LocalDateTime.now());
        adminFeign.update(updateAdmin);

        // 生成token
        String token = UUID.randomUUID().toString(true);

        // 存储token
        redisTemplate.opsForValue().set(
                RedisCachePrefixEnum.ADMIN_AUTH_LOGIN_TOKEN_TO_ID + token,
                admin.getId().toString(),
                tokenProperties.getExpireTime()
        );

        // 获取权限列表
        List<Long> roleIds = admin.getRoles().stream()
                .map(roleDTO -> roleDTO.getId())
                .collect(Collectors.toList());

        List<PermissionDTO> permissions = new ArrayList<>();
        if (!roleIds.isEmpty()) {
            permissions = permissionFeign.getByRoleIds(roleIds);
        }

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setAdmin(admin);
        loginVO.setPermissions(permissions);

        return ResultData.success(loginVO);
    }

    @ApiOperation("登出")
    @PostMapping("/logout")
    public ResultData logout(HttpServletRequest request) {
        // 从请求头获取token
        String tokenHeader = request.getHeader(tokenProperties.getHeader());
        if (StrUtil.isNotEmpty(tokenHeader) && tokenHeader.startsWith(tokenProperties.getPrefix())) {
            String token = tokenHeader.substring(tokenProperties.getPrefix().length());
            // 删除token
            redisTemplate.delete(RedisCachePrefixEnum.ADMIN_AUTH_LOGIN_TOKEN_TO_ID + token);
        }

        return ResultData.success();
    }
}