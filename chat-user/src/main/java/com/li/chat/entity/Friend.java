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
    @JoinColumn(name = "user_small_id", referencedColumnName = "id",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User userSmall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_big_id", referencedColumnName = "id",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User userBig;

    private String userSmallRemark;

    private String userBigRemark;

}
