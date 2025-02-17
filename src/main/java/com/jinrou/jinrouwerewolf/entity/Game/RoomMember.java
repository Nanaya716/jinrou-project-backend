package com.jinrou.jinrouwerewolf.entity.Game;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (RoomMember)实体类
 *
 * @author makejava
 * @since 2024-07-08 06:41:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("room_member")
public class RoomMember implements Serializable {
    private static final long serialVersionUID = -24756552300885692L;
    private Integer roomMemberId;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 房间内的几号玩家
     */
    private Integer playerId;
    /**
     * NOT_READY-未准备
     * READY-已准备
     * GAME_HAS_STARTED-房间曾经已开始
     */
    private UserReadyState userReadyState;
    /**
     * 职业表中的职业ID
     */
    private Integer userIdentityId;
    /**
     * 匿名ID
     */
    private String anonymousName;
    /**
     * 匿名头像url
     */
    private String anonymousIcon;
    /**
     * NOT_READY-平局
     * READY-胜利
     * GAME_HAS_STARTED-失败
     */
    private UserResult userResult;

    @Getter
    @AllArgsConstructor
    public enum UserReadyState {
        未准备("NOT_READY"),
        已准备("READY");

        @EnumValue
        private final String description;
    }

    @Getter
    @AllArgsConstructor
    public enum UserResult {
        平局("DRAW"),
        胜利("WIN"),
        失败("LOSE");
        @EnumValue
        private final String description;
    }

}

