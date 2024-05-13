package com.li.chat.netty.socketio;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author malaka
 */
// @Component
public class ServerRunner  implements CommandLineRunner {

    @Autowired(required = false)
    private SocketIOServer socketIOServer;


    @Override
    public void run(String... args) throws Exception {
        System.out.println(111111111);
        if (socketIOServer != null) {
            /*Optional.ofNullable(SpringService.getBean("messageEventHandler"))
                    .ifPresent(handler -> socketIOServer.getNamespace("/").addListeners(handler));*/

//            socketIOServer.getNamespace("/chat").addListeners(messageEventHandler);
            socketIOServer.start();
            System.out.println(114514);
        }
        System.out.println(123123);
    }

}
