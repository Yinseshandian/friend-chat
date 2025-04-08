package com.li.chat.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author malaka
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupDTO {

    private Long id;

    private String name;

    private String photo;

    private String introduction;

    private Integer memberSize;

    private Integer memberNum;

    private Long holderUserId;

    private Integer joinMode;

    private LocalDateTime createTime;

}
