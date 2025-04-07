package com.li.chat.service;

import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.vo.router.RouterVO;

import java.util.List;

/**
 * @author malaka
 */
public interface RouterService {
    /**
     * 构建前端路由
     */
    List<RouterVO> buildRouters(List<PermissionDTO> permissions);
}