package com.li.chat.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.expression.spel.ast.Operator;

import javax.persistence.*;

/**
 * @author malaka
 * 好友
 */
@Getter
@Setter
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "chat_friend")
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // // 程序层面控制 userId < friendId
    // private Long userId;
    // // 程序层面控制 userId < friendId
    // private Long friendId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", referencedColumnName = "id",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User friend;
    // 用户的备注 firend为当前用户 他的好友 user 的备注
    private String userRemark;
    // 好友的备注 user为当前用户 他的好友 friend 的备注
    private String friendRemark;

}
