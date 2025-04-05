package com.li.chat.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * @author malaka
 * 群组
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chat_group",
        indexes = {
                @Index(columnList = "name")
        })
public class Group extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String photo;

    private String introduction;

    private Integer memberSize;

    private Integer memberNum;

    private Long holderUserId;

    private Integer joinMode;

    @OneToMany(mappedBy = "group")
    private Set<GroupMember> GroupMember;

}
