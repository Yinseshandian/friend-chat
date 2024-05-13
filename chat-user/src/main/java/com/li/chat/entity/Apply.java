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
@Table(name = "chat_apply")
public class Apply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromId;

    private Long toId;

    private String message;

    private String remark;

    private Integer status; // 申请状态 0未处理 1同意 2拒绝


}
