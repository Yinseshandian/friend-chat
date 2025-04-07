package com.li.chat.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author malaka
 */
@ConfigurationProperties("friend-chat.admin.token")
@Getter
@Setter
public class TokenProperties {

    public String header = "Authorization";
    public String prefix;
    public Duration expireTime;

}
