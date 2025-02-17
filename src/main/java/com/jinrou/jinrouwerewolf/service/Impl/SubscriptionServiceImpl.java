package com.jinrou.jinrouwerewolf.service.Impl;

import com.jinrou.jinrouwerewolf.entity.WebSocketSessionStore;
import org.springframework.stereotype.Service;

/**
 * @Author: nanaya
 * @Date: 2025/01/07/3:17
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Service
public class SubscriptionServiceImpl {
    public void handleUnsubscribe(String sessionId, String subScribedId) {
        System.out.println("客户端取消订阅: " + "SessionId:" + sessionId + "subscribedId:" + subScribedId );
        // 移除订阅信息
            WebSocketSessionStore.removeSubscription(sessionId, subScribedId);
    }
    public void handleSubscribe(String sessionId, String subScribedId,String destination) {
        System.out.println("客户端订阅成功: " + destination + "SessionId:" + sessionId + "subscribedId:" + subScribedId );
        // 添加订阅信息
        if (destination != null) {
            WebSocketSessionStore.addSubscription(sessionId,subScribedId, destination);
        }
    }
}
