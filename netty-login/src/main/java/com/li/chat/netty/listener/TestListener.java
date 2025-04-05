package com.li.chat.netty.listener;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.feign.FriendFeign;
import com.li.chat.feign.UserFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author malaka
 */
// @Component
@Slf4j
public class TestListener  {

    @Resource
    private SocketIOServer socketIOServer;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private FriendFeign friendFeign;

    Map<Long, UUID> idToUUID = new HashMap<>();
    Map<UUID, Long> UUIDToId = new HashMap<>();

    @OnConnect
    public void eventOnConnect(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        log.info("链接开启，urlParams：{}", urlParams);
    }

    /**
     * 连接关闭
      */
    @OnDisconnect
    public void eventOnDisConnect(SocketIOClient client) {
        UUID sessionId = client.getSessionId();
        Long uid = UUIDToId.get(sessionId);
        log.info("eventOnDisConnect ---> 客户端唯一标识为：{}", sessionId);
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        //清除用户登录信息
        log.info("链接关闭，urlParams：{}", urlParams);

        idToUUID.remove(uid);
        UUIDToId.remove(sessionId);
    }


    /**
     * 用户上线
     */
    @OnEvent("online")
    public void online(SocketIOClient client, Map map) {
        String loginToken = map.get("loginToken") + "";
        Long uid = userFeign.checkLoginOnToken(loginToken);
        if (uid == null) {
            client.sendEvent("onlineResponse", "loginToken错误");
            return;
        }

        List<FriendDTO> friendList = friendFeign.list(uid, null);

        UUID sessionId = client.getSessionId();
        log.info("goOnline ---> 用户：{} 上线，UUID：{}", uid, sessionId);
        idToUUID.put(uid, sessionId);
        UUIDToId.put(sessionId, uid);
        client.sendEvent("onlineResponse", sessionId);
    }

    /**
     * 用户上线
     */
    @OnEvent("goOnline")
    public void goOnline(SocketIOClient client, Map map) {
        Long uid =Long.parseLong(""+map.get("uid"));
        UUID sessionId = client.getSessionId();
        log.info("goOnline ---> 用户：{} 上线，UUID：{}", uid, sessionId);
        idToUUID.put(uid, sessionId);
        UUIDToId.put(sessionId, uid);
        System.out.println(idToUUID);
    }

    /**
     * 用户下线
     */
    @OnEvent("goOffline")
    public void goOffline(SocketIOClient client, Map map) {
        Long uid = Long.parseLong(""+map.get("uid"));
        UUID sessionId = client.getSessionId();
        log.info("goOnline ---> 用户：{} 下线，UUID：{}", uid, sessionId);
        idToUUID.remove(uid);
        UUIDToId.remove(sessionId);
        client.disconnect();
    }

    /**
     * 发送数据
     */
    @OnEvent("send")
    public void send(SocketIOClient client, Map map) {
        Long toId = Long.parseLong(""+map.get("toId"));
        Long fromId = Long.parseLong(""+map.get("fromId"));
        UUID toUUID = idToUUID.get(toId);

        String msg = (String) map.get("msg");
        HashMap<Object, Object> result = new HashMap<>();
        result.put("from", fromId);
        result.put("msg", msg);
        SocketIOClient sc = socketIOServer.getClient(toUUID);
        sc.sendEvent("receiveMsg", result);
        log.info("用户{}发送消息给用户{}，内容：{}", fromId, toId, msg);

    }

}
