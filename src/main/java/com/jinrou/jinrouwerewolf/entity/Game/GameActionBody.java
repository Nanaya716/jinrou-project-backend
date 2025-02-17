package com.jinrou.jinrouwerewolf.entity.Game;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
*  游戏中承载具体系统操作的请求体，比如占卜，狼咬等，依据这个控制游戏逻辑
*  @Author:   nanaya
*  @Date:   2024/07/23/2:54
*  @Email:   qiyewuyin@gmail.com\714991699@qq.com
*  @QQ:   714991699
*  @Description:
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameActionBody {
    private String code;
    private Integer roomId;
    private Integer operatorOfPlayerId;
    private Integer targetOfPlayerId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date actionTime = new Date();
    private boolean gameStarted;
    private String dayOrNightOrVote;//
    private int dayNum;
    private long Seconds;//剩余时间
    private String identityName;
    private Message message;
    private Room room;
    private Boolean needVote;
    private Boolean needKami;
    private Boolean needUranai;
    private Boolean needKariudo;
    private List<Integer> partners;
    public GameActionBody(String code, int roomId) {
        this.code = code;
        this.roomId = roomId;
    }

    public GameActionBody(String code) {
        this.code = code;
    }

    public GameActionBody(String code,boolean gameStarted) {
        this.code = code;
        this.gameStarted = gameStarted;
    }

    public GameActionBody(String code, int roomId, int operatorOfPlayerId, Integer targetOfPlayerId) {
        this.code = code;
        this.roomId = roomId;
        this.operatorOfPlayerId = operatorOfPlayerId;
        this.targetOfPlayerId = targetOfPlayerId;
    }

    public GameActionBody(String code, Message message) {
        this.code = code;
        this.message = message;
    }

    public GameActionBody(String code, Message message, Room room) {
        this.code = code;
        this.message = message;
        this.room = room;
    }

    public GameActionBody(String code, Room room) {
        this.code = code;
        this.room = room;
    }
}
