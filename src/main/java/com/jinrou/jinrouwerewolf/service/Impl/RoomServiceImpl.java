package com.jinrou.jinrouwerewolf.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinrou.jinrouwerewolf.dao.RoomDao;
import com.jinrou.jinrouwerewolf.entity.Game.Room;
import com.jinrou.jinrouwerewolf.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: nanaya
 * @Date: 2024/07/08/7:02
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomDao, Room> implements RoomService {
    @Autowired
    private RoomDao roomDao;

    public Room getRoomById(int roomId) {
        return roomDao.queryById(roomId);
    }

    @Override
    public List<Room> getRoomsWithRoomStates(List<String> roomStates) {
        List<Room> list = roomDao.getRoomsWithRoomStates(roomStates);
        return list;
    }
    @Override
    public List<Room> getRoomsWithRoomStatesAndUserId(List<String> roomStates,Integer userId) {
        List<Room> list = roomDao.getRoomsWithRoomStatesAndUserId(roomStates,userId);
        return list;
    }


    @Override
    public Room createRoom(int userId) {
        return null;
    }

    @Override
    public List<Room> searchRoom(String keyword) {
        return null;
    }

    public Room getRoomAndPlayersById(int roomId) {
        Room room = roomDao.getRoomAndPlayersById(roomId);
        return room;
    }

    public int update(Room room) {
        return roomDao.update(room);
    }

}
