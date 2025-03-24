package com.li.chat.controller;

import com.li.chat.domain.DTO.CaptchaDTO;
import com.li.chat.common.utils.CaptchaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;

/**
 * @author malaka
 */
@RestController
@RequestMapping("/chat-base/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaUtils captchaUtils;
    /*
     * 获取图片验证码
     */
    @GetMapping
    public CaptchaDTO imgCaptchaToBase64(@RequestParam("key") String key) throws IOException, FontFormatException {
        CaptchaDTO captchaDTO = new CaptchaDTO();
        captchaDTO.setBase64Img(captchaUtils.createCatchaImgToBase64(key));
        return captchaDTO;
    }

    /**
     * 验证验证码
     */
    @PostMapping
    public boolean verifyRegister(@RequestParam("key")String key,
                                  @RequestParam("code") String code) {
        return captchaUtils.verification(key, code);
    }




}