package com.li.chat.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author malaka
 */
@Getter
@Setter
@Entity
@Table(name = "chat_group_member")
public class GroupMember extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    private Group group;

    private String nickname;

    @Column(columnDefinition = "tinyint default 0")
    private Integer type;//  类型，0成员，1管理员，2群主

}
