package com.li.chat.aspect;

import com.li.chat.annotation.RequiresPermission;
import com.li.chat.common.utils.RequestContext;
import com.li.chat.domain.admin.AdminDTO;
import com.li.chat.exception.NoPermissionException;
import com.li.chat.feign.admin.AdminFeign;
import com.li.chat.feign.admin.PermissionFeign;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 权限鉴定
 * @author malaka
 */

@Aspect
@Component
@Slf4j
public class PermissionAspect {

    private final PermissionFeign permissionFeign;
    private final AdminFeign adminFeign;

    public PermissionAspect(PermissionFeign permissionFeign, AdminFeign adminFeign) {
        this.permissionFeign = permissionFeign;
        this.adminFeign = adminFeign;
    }

    @Before("@annotation(com.li.chat.annotation.RequiresPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取当前登录用户ID
        Long adminId = RequestContext.getUserId();

        // 获取管理员信息
        AdminDTO admin = adminFeign.getById(adminId);

        // 超级管理员直接放行
        if (admin != null && admin.getRoles().stream()
                .anyMatch(role -> "admin".equals(role.getCode()))) {
            return;
        }

        // 获取方法上的RequiresPermission注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);

        // 获取需要的权限编码
        String permissionCode = requiresPermission.value();

        // 获取用户的权限列表
        Set<String> userPermissions = permissionFeign.getUserPermissionCodes(adminId);

        // 验证权限
        if (!userPermissions.contains(permissionCode)) {
            log.warn("用户[{}]没有权限[{}]访问[{}]", adminId, permissionCode, method.getName());
            throw new NoPermissionException(requiresPermission.message());
        }

        log.info("用户[{}]有权限[{}]访问[{}]", adminId, permissionCode, method.getName());
    }
}