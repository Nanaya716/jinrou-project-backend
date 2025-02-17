package com.jinrou.jinrouwerewolf.interceptor;

import com.alibaba.fastjson.JSON;
import com.jinrou.jinrouwerewolf.controller.ChatController;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.entity.WebSocketSessionStore;
import com.jinrou.jinrouwerewolf.service.Impl.SubscriptionServiceImpl;
import com.jinrou.jinrouwerewolf.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;

@Component
public class WebSocketConnectListener implements ApplicationListener<SessionConnectEvent> {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SubscriptionServiceImpl subscriptionService;

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        String token = null;
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        List<String> authorizationHeaders = headerAccessor.getNativeHeader("Authorization");

        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String authHeader = authorizationHeaders.get(0).trim();
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        User user = jwtUtil.getTokenBody(token);

        if (token != null && user != null) {
            WebSocketSessionStore.addSessionData(headerAccessor.getSessionId(), token, user);
        }
    }

    // 内部类：监听 WebSocket 断开事件
    @Component
    public static class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
        @Autowired
        private ChatController chatController;
        @Override
        public void onApplicationEvent(SessionDisconnectEvent event) {
            String sessionId = event.getSessionId();
            WebSocketSessionStore.removeSessionData(sessionId);
            System.out.println("WebSocket 已断开, sessionId: " + sessionId);
            chatController.OnlineUserUpdate("/topic/chatRoom");
        }
    }

    // 内部类：监听订阅事件
    @Component
    public static class WebSocketSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {
        @Autowired
        private SubscriptionServiceImpl subscriptionService;
        @Override
        public void onApplicationEvent(SessionSubscribeEvent event) {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
            String sessionId = headerAccessor.getSessionId();
            String destination = headerAccessor.getDestination();
            String subScribedId = headerAccessor.getSubscriptionId();

            if (destination != null) {
                if (sessionId != null)
                subscriptionService.handleSubscribe(sessionId, subScribedId ,destination);
            }
        }
    }

    // 内部类：监听取消订阅事件
    @Component
    public static class WebSocketUnsubscribeListener implements ApplicationListener<SessionUnsubscribeEvent> {
        @Autowired
        private SubscriptionServiceImpl subscriptionService;

        @Autowired
        private ChatController chatController;
        @Override
        public void onApplicationEvent(SessionUnsubscribeEvent event) {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
            String sessionId = headerAccessor.getSessionId();
            String subScribedId = headerAccessor.getSubscriptionId();

            if (subScribedId != null) {
                subscriptionService.handleUnsubscribe(sessionId, subScribedId);
                chatController.OnlineUserUpdate("/topic/chatRoom");
            }

        }
    }
}
