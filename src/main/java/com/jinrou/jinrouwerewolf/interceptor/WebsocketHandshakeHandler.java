package com.jinrou.jinrouwerewolf.interceptor;

import com.jinrou.jinrouwerewolf.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Component
public class WebsocketHandshakeHandler extends DefaultHandshakeHandler {
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 从请求头中提取 token
        // 使用 UriComponentsBuilder 解析查询参数
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
        String token = queryParams.getFirst("token");
        String userId = String.valueOf(jwtUtil.getTokenBody(token).getUserId());  // 解析后的用户id
        // 将 userId 存储到会话属性中
        attributes.put("userId", userId);

        if (userId == null) {
            return null;
        }

        // 创建并返回一个 Principal，将用户Id作为 WebSocket 会话的标识
        // 必须这样写，否则websocket的单独user频道会找不到对应的user
        return new Principal() {
            @Override
            public String getName() {
                //此时存的是userId，方法名需要重写所以是这个
                return userId;
            }
        };
    }
}
