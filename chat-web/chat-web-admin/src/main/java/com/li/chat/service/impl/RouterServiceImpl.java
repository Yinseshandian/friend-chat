package com.li.chat.service.impl;

import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.service.RouterService;
import com.li.chat.vo.router.MetaVO;
import com.li.chat.vo.router.RouterVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */

@Service
public class RouterServiceImpl implements RouterService {

    @Override
    public List<RouterVO> buildRouters(List<PermissionDTO> permissions) {
        List<RouterVO> routers = new ArrayList<>();

        // 先按parent_id分组
        for (PermissionDTO permission : permissions) {
            if (permission.getParentId() == 0) {
                RouterVO router = new RouterVO();
                router.setName(permission.getName());
                router.setPath(permission.getPath());
                router.setHidden(permission.getHidden());
                router.setComponent(permission.getComponent());
                router.setRedirect(permission.getRedirect());
                router.setAlwaysShow(permission.getAlwaysShow());
                router.setSort(permission.getSort());

                // 设置meta信息
                MetaVO meta = new MetaVO();
                meta.setTitle(permission.getName());
                meta.setIcon(permission.getIcon());
                meta.setNoCache(!permission.getKeepAlive());
                meta.setKeepAlive(permission.getKeepAlive());
                router.setMeta(meta);

                // 处理子路由
                List<PermissionDTO> children = getChildrenMenu(permission.getId(), permissions);
                router.setChildren(buildChildrenRouter(children, permissions));

                routers.add(router);
            }
        }

        // 按sort排序
        routers.sort(Comparator.comparing(RouterVO::getSort));

        return routers;
    }

    /**
     * 获取子菜单
     */
    private List<PermissionDTO> getChildrenMenu(Long parentId, List<PermissionDTO> permissions) {
        return permissions.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .sorted(Comparator.comparing(PermissionDTO::getSort))
                .collect(Collectors.toList());
    }

    /**
     * 构建子路由
     */
    private List<RouterVO> buildChildrenRouter(List<PermissionDTO> children, List<PermissionDTO> permissions) {
        List<RouterVO> childRouters = new ArrayList<>();

        if (children.isEmpty()) {
            return childRouters;
        }

        for (PermissionDTO child : children) {
            // 仅处理菜单和目录，按钮不处理
            if (child.getType() > 1) {
                continue;
            }

            RouterVO router = new RouterVO();
            router.setName(StringUtils.capitalize(child.getPath()));
            router.setPath(child.getPath());
            router.setComponent(child.getComponent());
            router.setHidden(child.getHidden());
            router.setRedirect(child.getRedirect());
            router.setAlwaysShow(child.getAlwaysShow());
            router.setSort(child.getSort());

            // 设置meta信息
            MetaVO meta = new MetaVO();
            meta.setTitle(child.getName());
            meta.setIcon(child.getIcon());
            meta.setNoCache(!child.getKeepAlive());
            meta.setKeepAlive(child.getKeepAlive());
            router.setMeta(meta);

            // 递归处理子路由
            List<PermissionDTO> grandChildren = getChildrenMenu(child.getId(), permissions);
            if (!grandChildren.isEmpty()) {
                router.setChildren(buildChildrenRouter(grandChildren, permissions));
            }

            childRouters.add(router);
        }

        return childRouters;
    }
}