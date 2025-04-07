package com.li.chat.admin.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author malaka
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chat_admin",
        indexes = {
                @Index(name = "idx_username", columnList = "username", unique = true)
        })
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(length = 64)
    private String nickname;

    private String avatar;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String mobile;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    private LocalDateTime lastLoginTime;

    @ManyToMany
    @JoinTable(
            name = "chat_admin_role",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}