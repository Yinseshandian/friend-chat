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
public class RoleDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer sort;
    private Boolean status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<PermissionDTO> permissions;
}