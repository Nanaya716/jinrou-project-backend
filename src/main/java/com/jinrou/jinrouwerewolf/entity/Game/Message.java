package com.jinrou.jinrouwerewolf.entity.Game;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import lombok.*;

import java.util.Date;
import java.io.Serializable;

/**
 * (Message)实体类
 *
 * @author makejava
 * @since 2024-07-08 06:46:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Message implements Serializable {
    private static final long serialVersionUID = -47903053063759157L;
    /**
     * 消息ID
     */
    private Integer messageId;
    /**
     * 消息所在的房间ID
     */
    private Integer roomId;
    /**
     * 消息发出者ID
     */
    private Integer userId;
    private String playerName;
    /**
     * GM-GM消息 搭配messageGmToPlayerId使用
     * GM-ALL-GM全体消息
     * ALL-全体消息
     * SYSTEM-系统消息
     * DAYTALK-白天正常讨论
     * WOLFTALK-狼频夜晚讨论
     * KYOUYUUTALK-共有夜晚讨论
     * KITSUNETALK-妖狐夜晚讨论
     * WATCHTALK-观战讨论
     * SELFTALK-自言自语
     * UNDERTALK-灵界讨论
     */
    private String messageType;
    /**
     * 发言编号 第x个发言的
     */
    private int messageIndex;
    /**
     * GM对话玩家编号（playerId
     */
    private Integer messageGmToPlayerId;
    /**
     * 消息发布时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date messageTime = new Date();

    private String messageContent;
    // 新增字段
    private String fontColor; // 消息字体颜色，存储十六进制值，如 #ff0000
    private String fontSize;  // 消息字体大小，存储如 "14px"
    private String dayTime;  //游戏内消息，N1 D2 N3等 开始前为0，结束后为1
    private Integer replyTo;
    private User user;

    public static Message system(Integer roomId, String messageContent, RedisService redisService,boolean Incr){
        Message message = new Message();
        message.setRoomId(roomId);
        message.setMessageType("SYSTEM");
//        message.setMessageTime(new Date());
        message.setMessageContent(messageContent);
        if(Incr){
            message.setMessageIndex(redisService.getNextMessageIndexAndIncr(String.valueOf(message.getRoomId())));
        }else{
            message.setMessageIndex(redisService.getNextMessageIndex(String.valueOf(message.getRoomId())));
        }
        return message;
    }

    public static Message system(Integer roomId, String messageContent, RedisService redisService,boolean Incr, Integer messageGmToPlayerId){
        Message message = new Message();
        message.setRoomId(roomId);
        message.setMessageType("SYSTEM");
        message.setMessageGmToPlayerId(messageGmToPlayerId);
        message.setMessageContent(messageContent);
        if(Incr){
            message.setMessageIndex(redisService.getNextMessageIndexAndIncr(String.valueOf(message.getRoomId())));
        }else{
            message.setMessageIndex(redisService.getNextMessageIndex(String.valueOf(message.getRoomId())));
        }
        return message;
    }

    public static Message wolftalk(Integer roomId, String messageContent, RedisService redisService,boolean Incr, Integer messageGmToPlayerId){
        Message message = new Message();
        message.setRoomId(roomId);
        message.setMessageType("WOLFTALK");
        message.setReplyTo(messageGmToPlayerId);
        message.setMessageContent(messageContent);
        if(Incr){
            message.setMessageIndex(redisService.getNextMessageIndexAndIncr(String.valueOf(message.getRoomId())));
        }else{
            message.setMessageIndex(redisService.getNextMessageIndex(String.valueOf(message.getRoomId())));
        }
        return message;
    }
}

