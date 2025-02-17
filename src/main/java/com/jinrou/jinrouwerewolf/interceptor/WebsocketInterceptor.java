package com.jinrou.jinrouwerewolf.interceptor;

/**
 * @Author: nanaya
 * @Date: 2024/07/22/5:29
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * <websocke消息监听，用于监听websocket用户连接情况>
 * <功能详细描述>
 **/
@Slf4j


public class WebsocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        return true; // 握手失败
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}

