package com.li.chat.domain.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author malaka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private String path;
    private String component;
    private String redirect;
    private String icon;
    private Integer type;
    private Boolean hidden;
    private Boolean alwaysShow;
    private Boolean keepAlive;
    private Integer sort;
    private Boolean status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<PermissionDTO> children;
}