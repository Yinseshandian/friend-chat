package com.li.chat.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author malaka
 */
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseEntity {

    @Column(name = "create_time",
            insertable = false,
            updatable = false,
            columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    protected LocalDateTime createTime;

    @Column(name = "update_time",
            insertable = false,
            updatable = false,
            columnDefinition = "timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    protected LocalDateTime updateTime;

}
