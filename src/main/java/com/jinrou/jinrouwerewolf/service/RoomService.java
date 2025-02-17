package com.jinrou.jinrouwerewolf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinrou.jinrouwerewolf.entity.Game.Room;

import java.util.List;

/**
 * @Author: nanaya
 * @Date: 2024/07/08/6:18
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
public interface RoomService extends IService<Room> {
    Room getRoomById(int id);
    List<Room> getRoomsWithRoomStates(List<String> roomStates);
    List<Room> getRoomsWithRoomStatesAndUserId(List<String> roomStates,Integer userId);
    Room createRoom(int userId);
    List<Room> searchRoom(String keyword);
    Room getRoomAndPlayersById(int roomId);
    int update(Room room);
}
