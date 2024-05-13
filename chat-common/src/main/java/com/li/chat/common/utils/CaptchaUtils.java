package com.li.chat.common.utils;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author malaka
 */
@Component
public class CaptchaUtils {

    @Autowired
    private RedisCache redisCache;

    private final static int CACHE_TIME_MINUTES = 2;

    private final static String KEY_PRE = "FRIEND_CHAT_CAPTCHA";

    /**
     * 验证
     * @param key uuid唯一标识
     * @param code 输入验证码
     * @return 验证通过
     */
    public boolean verification(String key, @NotNull String code) {
        key = getKey(key);
        if (redisCache.verifyKey(key)) {
            // 获取缓存的验证码
            String cacheCode = redisCache.getCacheObject(key);
            // 删除验证码
            redisCache.deleteObject(key);
            // 验证输入
            return code.equalsIgnoreCase(cacheCode);
        }else {
            return false;
        }
    }

    public String createCatchaImgToBase64(String key) throws IOException, FontFormatException {
        //生成文字验证码
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        specCaptcha.setFont(Captcha.FONT_1);
        String text = specCaptcha.text().toLowerCase();

        //生成token
        System.out.println("\"key\": \"" + key + "\", \n" +
                "    \"verifyCode\": \"" + text + "\"");
        createToken(key, text);
        return specCaptcha.toBase64("");
    }

    /**
     * 创建token
     * @param key 键
     * @param text 验证码
     */
    private void createToken(String key, String text) {
        key = getKey(key);
        redisCache.setCacheObject(key, text, CACHE_TIME_MINUTES, TimeUnit.MINUTES);
    }


    /**
     * 组装key
     * @param key key
     * @return key
     */
    private String getKey(String key) {
        return KEY_PRE + key;
    }


}
