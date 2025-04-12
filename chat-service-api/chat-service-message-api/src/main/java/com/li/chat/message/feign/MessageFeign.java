package com.li.chat.message.feign;

import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.message.MessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author malaka
 */
@FeignClient(name = "chat-message", contextId="message")
@RequestMapping("/chat-message/message")
public interface MessageFeign {

    @GetMapping("/search")
    PageResultData<MessageDTO> search(@SpringQueryMap MessageDTO messageDTO,
                                      @RequestParam("pageNum") int pageNum,
                                      @RequestParam("pageSize") int pageSize);

    @GetMapping("/info/{id}")
    public MessageDTO getDetail(@PathVariable("id") Long id);

}