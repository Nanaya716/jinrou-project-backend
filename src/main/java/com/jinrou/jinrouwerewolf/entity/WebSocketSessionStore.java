package com.jinrou.jinrouwerewolf.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import lombok.*;

import java.io.Serializable;

public class WebSocketSessionStore {

    // 用来存储 sessionId和 WebsocketSessions 映射
    private static final ConcurrentHashMap<String, WebsocketSessions> sessionData = new ConcurrentHashMap<>();

    // 添加会话数据
    public static void addSessionData(String sessionId, String token, User user) {
        sessionData.put(sessionId, new WebsocketSessions(sessionId, token, user, new HashMap<>()));
    }

    // 获取会话数据
    public static WebsocketSessions getWebsocketSessions(String sessionId) {
        return sessionData.get(sessionId);
    }

    public static ConcurrentHashMap<String, WebsocketSessions> getSessionData() {
        return sessionData;
    }

    // 添加订阅信息
    public static void addSubscription(String sessionId, String subscriptionId, String destination) {
        WebsocketSessions session = sessionData.get(sessionId);
        if (session != null) {
            session.getSubscribedDestinations().put(subscriptionId, destination);
        }
    }

    // 移除订阅信息
    public static void removeSubscription(String sessionId, String subscriptionId) {
        WebsocketSessions session = sessionData.get(sessionId);
        if (session != null) {
            session.getSubscribedDestinations().remove(subscriptionId);
        }
    }

    // 获取订阅某个路径的所有用户
    public static List<User> getUsersByDestination(String destination) {
        List<User> users = new ArrayList<>();
        for (WebsocketSessions session : sessionData.values()) {
            if (session.getSubscribedDestinations().containsValue(destination)) {
                users.add(session.getUser());
            }
        }
        return users;
    }

    // 获取所有用户
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (WebsocketSessions session : sessionData.values()) {
            User user = session.getUser();
            user.clearSensitiveData();
            users.add(user);
        }
        return users;
    }

    // 删除会话数据
    public static void removeSessionData(String sessionId) {
        sessionData.remove(sessionId);
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class WebsocketSessions implements Serializable {
        private String sessionId;  // 或者可以改为 token，取决于您希望如何索引会话
        private String token;
        private User user;
        // key是订阅id ，value是订阅的路径
        private Map<String, String> subscribedDestinations;

        public WebsocketSessions(String sessionId, String token, User user) {
            this.sessionId = sessionId;
            this.token = token;
            this.user = user;
            this.subscribedDestinations = new HashMap<>();
        }
    }
}
