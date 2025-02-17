package com.jinrou.jinrouwerewolf.entity.Game;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jinrou.jinrouwerewolf.entity.User;
import lombok.*;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * (Room)实体类
 *
 * @author makejava
 * @since 2024-07-08 06:41:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("room")
@Getter
@Setter
public class Room implements Serializable,Cloneable {
    private static final long serialVersionUID = -33138738039982798L;
    /**
     * 房间ID
     */
    @TableId(type = IdType.AUTO)
    private int roomId;
    /**
     * 房间状态
     * NORMAL-正常
     * OLD-陈旧
     * PLAYING-进行中
     * ENDED-已结束
     * DISCARDED-已废村
     */
    private String roomState;

    /**
     * 替身君总是在controller层确定设置后开始 再决定加入与否
     */
    @TableField(exist = false)
    private List<Player> players = new ArrayList<>();
    /**
     * 游戏设置
     */
    @TableField(exist = false)
    private GameSettings gameSettings;
    /**
     * 房主
     */
    private Integer roomCreatorId;
    private Integer gameSettingId;
    /**
     * 胜利者
     * DRAW-平局
     * MURABITO-村人
     * JINROU-人狼
     * KITSUNE-妖狐
     */
    private String winner;
    /**
     * 房间创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date roomCreateTime;
    /**
     * 房间结束时间（游戏结束或废村
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date roomEndTime;
    /**
     * 房间标题
     */
    private String roomTitle;
    /**
     * 房间描述
     */
    private String roomDescription;

    /**
     * NO-不匿名
     * WEAK-弱匿名
     * STRONG-强匿名
     */
    private String isAnonymous;

    /**
     * f-无密码
     * t-有密码
     */
    private Boolean isLocked;

    /**
     * 有密码的情况下的房间密码
     */

    private String roomPassword;
    private String villageRule;

    @TableField(exist = false)
    private GameInfo gameInfo;

    @TableField(exist = false)
    private Map<Long, List<String>> playerMessages;

    private User user;


    public Player getPlayerByUserId(Integer userId){
        Player getedPlayer = this.getPlayers().stream()
                .filter(player -> player.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        return getedPlayer;
    }

    public Player getPlayerByRoomPlayerId(Integer roomPlayerId){
        Player getedPlayer = this.getPlayers().stream()
                .filter(player -> player.getRoomPlayerId().equals(roomPlayerId))
                .findFirst()
                .orElse(null);
        return getedPlayer;
    }



    public boolean GmInRoom() {
        for (Player player : this.getPlayers()) {
            if (player.getIdentity().getName().equals("GM")) {
                return true;
            }
        }
        return false;
    }

    //深拷贝room
    public Room(Room room) {
        this.roomId = room.roomId;
        this.roomState = room.roomState;
        this.roomCreatorId = room.roomCreatorId;
        this.gameSettingId = room.gameSettingId;
        this.winner = room.winner;
        this.roomCreateTime = room.roomCreateTime;
        this.roomEndTime = room.roomEndTime;
        this.roomTitle = room.roomTitle;
        this.roomDescription = room.roomDescription;
        this.isAnonymous = room.isAnonymous;
        this.isLocked = room.isLocked;
        this.roomPassword = room.roomPassword;
        this.villageRule = room.villageRule;
        this.gameSettings = room.gameSettings;
        this.gameInfo = room.gameInfo;
        this.playerMessages = room.playerMessages;
        this.user = room.user;

        // 深拷贝 players
        this.players = new ArrayList<>();
        for (Player player : room.getPlayers()) {
            Player playerCopy = new Player(player); // 假设 Player 也有拷贝构造函数
            this.players.add(playerCopy);
        }
    }


    public Room PlayersWithIdentityNull() {
        // 创建一个 Room 副本并修改 players 的 identity
        Room roomCopy = new Room(this); // 创建副本
        for (Player player : roomCopy.getPlayers()) {
            player.setIdentity(null); // 修改 identity
        }
        return roomCopy;
    }





//    public Room getPlayersWithIdentityNull(){
//        Room room = new Room(this);
//        room.setPlayersWithIdentityNull();
//        return room;
//        return this.
//    }
}

