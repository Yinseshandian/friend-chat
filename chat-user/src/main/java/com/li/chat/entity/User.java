package com.li.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @author malaka
 * 用户
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(name = "chat_user")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    @ColumnTransformer(
            write = "HEX(AES_ENCRYPT(?, 'key_pwdmalaka'))",
            read = "CAST(AES_DECRYPT(UNHEX(password),'key_pwdmalaka') as char)"
    )
    private String password;

    @Column(unique = true)
    private String nickname;

    private String avatar;

    private String signature;

    @Column(columnDefinition = "tinyint default 0")
    private Integer sex; // 0：保密，1：男，2：女

    @Column(columnDefinition = "tinyint default 0")
    private Integer status; // 0：正常，1：冻结，2：注销

    @OneToMany(mappedBy = "user")
    private Set<Friend> friendsU;

    @OneToMany(mappedBy = "friend")
    private Set<Friend> friendsF;

}
