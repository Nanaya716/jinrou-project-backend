package com.jinrou.jinrouwerewolf.entity.Game;

/**
 * @Author: nanaya
 * @Date: 2024/07/19/20:22
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:数据暂存区 进行中的游戏才需要 未进行的只是聊天室
 */

import java.util.HashMap;
import java.util.Map;

public class GlobalData {

    private volatile static GlobalData instance;
    /**
     * roomId，room对象
     * 每个房间占一个位置 废弃、游戏结束均移除
     */
    private Map<Integer, Room> roomData = new HashMap<>();

    /**
     * roomId，GameInfo对象
     * 每个房间占一个位置 游戏开始时加入，游戏结束移除
     */
    private Map<Integer, GameInfo> gameData = new HashMap<>();

    public static GlobalData getInstance(){
        if(instance==null){
            synchronized (GlobalData.class){
                if(instance==null){
                    instance =new GlobalData();
                }
            }
        }
        return instance;
    }

    public Map<Integer, Room> getRoomData() {
        return roomData;
    }

    public void putRoomData(Integer integer,Room room){
        roomData.put(integer,room);
    }

    public Map<Integer, GameInfo> getGameData() {
        return gameData;
    }

    public void putGameData(int roomId,GameInfo gameInfo) {
        this.gameData.put(roomId,gameInfo);
    }
}
