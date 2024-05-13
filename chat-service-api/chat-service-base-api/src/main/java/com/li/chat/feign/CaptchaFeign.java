package com.li.chat.feign;

import com.li.chat.domain.DTO.CaptchaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 验证码
 * @author malaka
 */
@FeignClient(name = "chat-base", contextId = "captcha")
@RequestMapping("/captcha")
public interface CaptchaFeign {

    /**
     * 获取注册图片验证码
     */
    @GetMapping
    CaptchaDTO imgCaptchaToBase64(@RequestParam("key")String key) ;

    /**
     * 验证注册验证码
     */
    @PostMapping
    boolean verifyRegister(@RequestParam("key")String key,
                           @RequestParam("code") String code);


}
