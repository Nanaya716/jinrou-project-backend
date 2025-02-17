package com.jinrou.jinrouwerewolf.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinrou.jinrouwerewolf.entity.Game.Room;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (Room)表数据库访问层
 *
 * @author makejava
 * @since 2024-07-08 08:36:07
 */
@Mapper
@Repository
public interface RoomDao extends BaseMapper<Room> {
    List<Room> getRoomsWithRoomStates(List<String> roomStates);
    List<Room> getRoomsWithRoomStatesAndUserId(List<String> roomStates,Integer userId);
    Room queryById(int id);
    Room getRoomAndPlayersById(int id);
    int update(Room room);

}

