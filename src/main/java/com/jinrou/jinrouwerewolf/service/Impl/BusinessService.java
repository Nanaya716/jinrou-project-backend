package com.jinrou.jinrouwerewolf.service.Impl;

import com.jinrou.jinrouwerewolf.entity.Game.GlobalData;
import com.jinrou.jinrouwerewolf.entity.Game.Room;
import com.jinrou.jinrouwerewolf.entity.User;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: nanaya
 * @Date: 2024/07/21/5:42
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
public class BusinessService {

    private GlobalData globalData = GlobalData.getInstance();

    private int roomId;

    private Lock lock = new ReentrantLock();

    /**
     * 创建房间
     * @param creator
     * @param title
     * @param description
     * @param isGmMode
     * @param isLocked
     * @param password
     * @return
     */
    public Room createRoom(User creator,
                           String title,
                           String description,
                           boolean isGmMode,
                           boolean isLocked,
                           String password,
                           String isAnonymous) {
        lock.lock();
        try {
            roomId = 10000; //TODO 从数据库中获取目前应有的房间号 此为测试
        } finally {
            lock.unlock();
        }
        Room room = new Room();
        room.setRoomId(roomId);
        room.setRoomTitle(title);
        room.setRoomDescription(description);
        room.setIsLocked(isLocked);
        if(isLocked){
            room.setRoomPassword(password);
        }
        room.setIsAnonymous(isAnonymous);
        globalData.putRoomData(roomId, room);
//        globalData.putGameData(roomId,new GameInfo());
        return room;
    }


    /**
     * 搜索房间
     * @param roomId 房间号
     * @return 0表示进入成功，-1表示没有此房间，-2表示此房间已满
     */
    public Room searchRoom(int roomId) {
        //TODO 数据库中查询
        return  globalData.getRoomData().get(roomId);

    }
}
