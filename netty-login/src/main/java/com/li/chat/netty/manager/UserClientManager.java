package com.li.chat.netty.manager;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author malaka
 */
@Component
public class UserClientManager {

    // 存储用户ID与客户端集合的映射（线程安全）
    private final Map<Long, Set<SocketIOClient>> userClients = new HashMap<>();

    private UserClientManager() {}


    /**
     * 添加用户客户端映射
     */
    public void addClient(Long userId, SocketIOClient client) {
        userClients.compute(userId, (key, clients) -> {
            if (clients == null) {
                // 使用线程安全的Set
                clients = new HashSet<>();
            }
            clients.add(client);
            return clients;
        });
    }

    /**
     * 移除用户客户端映射
     */
    public void removeClient(Long userId, SocketIOClient client) {
        if (userId == null){
            return;
        }
        userClients.computeIfPresent(userId, (key, clients) -> {
            clients.remove(client);
            // 若该用户无其他客户端，删除整个键
            return clients.isEmpty() ? null : clients;
        });
    }

    /**
     * 获取用户的所有客户端
     */
    public Set<SocketIOClient> getClients(Long userId) {
        return userClients.getOrDefault(userId, Collections.emptySet());
    }
}