package com.jinrou.jinrouwerewolf.entity.Game;

import com.jinrou.jinrouwerewolf.entity.Identity.Identity;
import com.jinrou.jinrouwerewolf.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: nanaya
 * @Date: 2024/07/16/19:16
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Player implements Cloneable {
    private Integer playerId;
    /**
     * 和用户id一对一关系
     */
    private Integer userId;
    /**
     * 场内编号
     */
    private Integer roomPlayerId;
    /**
     * 场内匿名名字
     */
    private String name;
    /**
     * 匿名头像url
     */
    private String iconUrl;
    private Integer roomId;
    private Identity identity;
    private Boolean isAlive;
    private Boolean isReady;
    private String result;

    public static Player createFirstVictim(int roomId){
        Player player = new Player();
        player.setRoomPlayerId(0);
        player.setUserId(0);
        player.setName("初日牺牲者");
        player.setRoomId(roomId);
        player.setIsAlive(true);
        player.setIsReady(true);
        return player;
    }

    public Player(Player player) {
        this.playerId = player.playerId;
        this.userId = player.userId;
        this.roomPlayerId = player.roomPlayerId;
        this.name = player.name;
        this.iconUrl = player.iconUrl;
        this.roomId = player.roomId;
        this.isAlive = player.isAlive;
        this.isReady = player.isReady;
    }

}