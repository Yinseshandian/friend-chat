package com.li.chat.netty.autoconfigure.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

/**
 * @author malaka
 */
@ConfigurationProperties("friend-chat.message.offline")
@Getter
@Setter
public class OfflineMsgProperties {

    private int expired;

    private TimeUnit timeUnit;

}
