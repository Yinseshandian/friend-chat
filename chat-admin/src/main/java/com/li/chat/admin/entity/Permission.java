package com.li.chat.admin.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chat_permission",
        indexes = {
                @Index(name = "idx_code", columnList = "code", unique = true),
                @Index(name = "idx_parent_id", columnList = "parent_id")
        })
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id", columnDefinition = "bigint(20) default 0")
    private Long parentId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 64)
    private String code;

    private String path;

    private String component;

    private String redirect;

    @Column(length = 100)
    private String icon;

    @Column(nullable = false)
    private Integer type; // 0-目录, 1-菜单, 2-按钮

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean hidden;

    @Column(name = "always_show", columnDefinition = "tinyint(1) default 0")
    private Boolean alwaysShow;

    @Column(name = "keep_alive", columnDefinition = "tinyint(1) default 0")
    private Boolean keepAlive;

    private Integer sort;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}