package com.li.chat.feign;

import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.common.utils.PageResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author malaka
 */
@FeignClient(name = "chat-user", contextId = "test")
public interface TestFeign {

    @RequestMapping("/test")
    PageResultData<UserDTO> test();

}
