package com.jinrou.jinrouwerewolf.conf;
import com.jinrou.jinrouwerewolf.interceptor.WebsocketHandshakeHandler;
import com.jinrou.jinrouwerewolf.interceptor.WebsocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: nanaya
 * @Date: 2024/07/14/18:47
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Configuration
@EnableWebSocketMessageBroker  //启用消息代理机制，启用后必须重写registerStompEndpoints方法
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Autowired
    private WebsocketHandshakeHandler handshakeHandler;

    // 添加这个Endpoint，这样在网页中就可以通过websocket连接上服务,
    // 也就是我们配置websocket的服务地址,并且可以指定是否使用socketjs
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/wsConnect")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(handshakeHandler)
                .addInterceptors(new WebsocketInterceptor()) // 添加自定义握手拦截器
                .withSockJS(); // 注册 WebSocket 端点
    }


//    /**
//     * 这个bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
//     * 如果不设置，只能建立连接而无法从WebSocketServer中发送信息进行确认
//     * @return
//     */
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter(){
//        return new ServerEndpointExporter();
//    }
// 配置消息代理，哪种路径的消息会进行代理处理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // 自定义调度器，用于控制心跳线程
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 线程池线程数，心跳连接开线程
        taskScheduler.setPoolSize(1);
        // 线程名前缀
        taskScheduler.setThreadNamePrefix("websocket-heartbeat-thread-");
        // 初始化
        taskScheduler.initialize();

        /**
         * spring 内置broker对象
         * 1. 配置代理域，可以配置多个，此段代码配置代理目的地的前缀为 /topic
         *    我们就可以在配置的域上向客户端推送消息
         * 2，进行心跳设置，第一值表示server最小能保证发的心跳间隔毫秒数, 第二个值代码server希望client发的心跳间隔毫秒数
         * 3. 可以配置心跳线程调度器 setHeartbeatValue要配合setTaskScheduler才可以生效
         *    调度器我们可以自己写一个，也可以自己使用默认的调度器 new DefaultManagedTaskScheduler()
         */
        config.enableSimpleBroker("/topic","/queue") // 配置消息代理，用于订阅
                .setHeartbeatValue(new long[]{8000,8000})
                .setTaskScheduler(taskScheduler);
        /**
         *  "/app" 为配置应用服务器的地址前缀，表示所有以/app 开头的客户端消息或请求
         *  都会路由到带有@MessageMapping 注解的方法中
         */
            config.setApplicationDestinationPrefixes("/app");

        /**
         *  1. 配置一对一消息前缀， 客户端接收一对一消息需要配置的前缀 如“'/user/'+userid + '/message'”，
         *     是客户端订阅一对一消息的地址 stompClient.subscribe js方法调用的地址
         *  2. 使用@SendToUser发送私信的规则不是这个参数设定，在框架内部是用UserDestinationMessageHandler处理，
         *     而不是 AnnotationMethodMessageHandler 或  SimpleBrokerMessageHandler
         *     or StompBrokerRelayMessageHandler，是在@SendToUser的URL前加“user+sessionId"组成
         */

            config.setUserDestinationPrefix("/user");
    }

    /**
     * 拦截器加入 spring ioc容器
     * @return
     */
//    @Bean
//    public WebSocketChannelInterceptor webSocketChannelInterceptor()
//    {
//        return new WebSocketChannelInterceptor();
//    }


//    public void configureClientInboundChannel(ChannelRegistration registration){
//        registration.interceptors(webSocketChannelInterceptor());
//    }
    /**
     * 配置发送与接收的消息参数，可以指定消息字节大小，缓存大小，发送超时时间
     * @param registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        /*
         * 1. setMessageSizeLimit 设置消息缓存的字节数大小 字节
         * 2. setSendBufferSizeLimit 设置websocket会话时，缓存的大小 字节
         * 3. setSendTimeLimit 设置消息发送会话超时时间，毫秒
         */
        registration.setMessageSizeLimit(1024000)
                .setSendBufferSizeLimit(65536000) // 640k
                .setSendTimeLimit(800000);
    }



}



