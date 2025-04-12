package com.li.chat.controller;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.param.PageParam;
import com.li.chat.common.utils.PageResultData;
import com.li.chat.domain.DTO.message.MessageDTO;
import com.li.chat.entity.HistoryMessage;
import com.li.chat.service.HistoryMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@RestController
@RequestMapping("/chat-message/message")
public class HistoryMessageController {

    @Autowired
    private HistoryMessageService historyMessageService;


    @GetMapping("/search")
    PageResultData<MessageDTO> search(@SpringQueryMap MessageDTO messageDTO,
                                      @RequestParam("pageNum") int pageNum,
                                      @RequestParam("pageSize") int pageSize) {
        PageParam pageParam = PageParam.builder().pageNum(pageNum).pageSize(pageSize).build();
        Page<HistoryMessage> page = historyMessageService.findByMessageDTO(messageDTO, pageParam);
        List<MessageDTO> list = page.stream().map(v -> {
            MessageDTO dto = MessageDTO.builder().build();
            BeanUtil.copyProperties(v, dto);
            return dto;
        }).collect(Collectors.toList());

        return PageResultData.<MessageDTO>builder()
                .total(page.getTotalElements())
                .rows(list)
                .pageSize(pageSize)
                .pageNum(pageNum).build();
    }

    @GetMapping("/info/{id}")
    public MessageDTO getDetail(@PathVariable("id") Long id) {
        HistoryMessage historyMessage = historyMessageService.findById(id);
        MessageDTO dto = MessageDTO.builder().build();
        BeanUtil.copyProperties(historyMessage, dto);
        return dto;
    }

}