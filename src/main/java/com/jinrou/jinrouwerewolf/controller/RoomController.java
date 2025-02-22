package com.jinrou.jinrouwerewolf.controller;

import com.jinrou.jinrouwerewolf.entity.Game.*;
import com.jinrou.jinrouwerewolf.entity.Result;
import com.jinrou.jinrouwerewolf.service.GameSettingsService;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import com.jinrou.jinrouwerewolf.service.MessageService;
import com.jinrou.jinrouwerewolf.service.PlayerService;
import com.jinrou.jinrouwerewolf.service.RoomService;
import com.jinrou.jinrouwerewolf.util.ConstConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: nanaya
 * @Date: 2024/07/08/15:46
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameSettingsService gameSettingsService;
    @Autowired
    private MessageService messageService;

    /**
     * 根据状态获取房间列表
     *
     * @param states
     * @return
     */
    @GetMapping("/getRooms")
    public Result getRoomsWithStatus(@RequestParam(value = "states", required = true) String states) {
        // 将逗号分隔的字符串转换为 List<String>
        List<String> statesList = Arrays.asList(states.split(","));
        List<Room> getedRooms = roomService.getRoomsWithRoomStates(statesList);
        return Result.success(getedRooms);
    }

    @GetMapping("/getRoomsWithUserId")
    public Result getRoomsWithStatus(@RequestParam(value = "states", required = true) String states,@RequestParam(value = "userId", required = true) Integer userId) {
        // 将逗号分隔的字符串转换为 List<String>
        List<String> statesList = Arrays.asList(states.split(","));
        List<Room> getedRooms = roomService.getRoomsWithRoomStatesAndUserId(statesList,userId);
        return Result.success(getedRooms);
    }

    @GetMapping("/getRoomAndPlayersById")
    public Result getRoomAndPlayersById(@RequestParam(value = "roomId", required = true) int roomId) {
        //room表
        Room getedRoom = roomService.getRoomAndPlayersById(roomId);
        return Result.success(getedRoom);
    }

    /**
     * 创建房间
     *
     * @param room
     * @return
     */
    @PostMapping("/createRoom")
    public Result createRoom(@RequestBody Room room) {
        room.setRoomCreateTime(new Date());
        room.setRoomState("NORMAL");

        GameSettings gameSettings = new GameSettings();
        gameSettings.setIsFirstVictims(true);
        gameSettings.setIsHopeMode(false);
        gameSettings.setNightDuration(120);
        gameSettings.setDayDuration(120);
        gameSettings.setVoteDuration(120);
        gameSettings.setMorningDuration(120);
        gameSettings.setNSecondRule(15);
        gameSettings.setHunterContinuousGuarding(true);
        gameSettingsService.save(gameSettings);
        //获取gamesettings回调主键
        Integer gameSettingsId = gameSettings.getGameSettingId();
        room.setGameSettingId(gameSettingsId);
        roomService.save(room);
        // 获取回填的主键
        Integer roomId = room.getRoomId();
        return Result.success(roomId);
    }

    @PostMapping("/joinRoom")
    public Result joinRoom(@RequestBody Player player) {
        Player getedPlayer = playerService.queryByRoomIdAndUserId(player.getRoomId(), player.getUserId());
        if(getedPlayer != null){
            return Result.error("加入失败，您已经加入该房间","加入失败，您已经加入该房间");
        }
        Room room = roomService.getRoomAndPlayersById(player.getRoomId());

        if (room.getPlayers().stream().anyMatch(p -> p.getName().equals(player.getName()))) {
            return Result.error("加入失败，该房间已有同名玩家", "加入失败，该房间已有同名玩家");
        }

        if (room.getPlayers().size() >= 30) {
            return Result.error("加入失败，该房间人数已满","加入失败，该房间人数已满");
        }
        player.setRoomPlayerId(redisService.generatePlayerId(player.getRoomId()).intValue());
        player.setIsAlive(true);
        player.setIsReady(false);
        playerService.save(player);
        // 获取回填的主键
        Integer playerId = player.getPlayerId();

        GameActionBody gameActionBody = new GameActionBody(ConstConfig.ROOM_SOMEONE_JOIN_ROOM, player.getRoomId());
        //构造actionbody的Message
        Message message = Message.system(player.getRoomId(), player.getName() + "加入了游戏。",redisService,true);
        message.setUserId(player.getUserId());
        gameActionBody.setMessage(message);
        gameActionBody.setRoom(roomService.getRoomAndPlayersById(player.getRoomId()));
        if (playerId != null) {
            messagingTemplate.convertAndSend("/topic/room/" + player.getRoomId() + "/ALL", gameActionBody);
            messageService.save(message);
        }
        return Result.success(player.getName());
    }

    @PostMapping("/leftRoom")
    public Result leftRoom(@RequestBody Player player) {
        player.setRoomPlayerId(redisService.generatePlayerId(player.getRoomId()).intValue());
        player.setIsAlive(true);
        player.setIsReady(false);
        Player QueryedPlayer = playerService.QueryByUserIdAndRoomId(player.getUserId(), player.getRoomId());
        player.setName(QueryedPlayer.getName());
        // 使用条件构造器
        playerService.removeByUserIdAndRoomId(player.getUserId(), player.getRoomId());
        player.setName(player.getName());
        GameActionBody gameActionBody = new GameActionBody(ConstConfig.ROOM_SOMEONE_LEFT_ROOM, player.getRoomId());
        //构造actionbody的Message
        Message message = Message.system(player.getRoomId(), player.getName() + "离开了游戏。",redisService,true);
        message.setUserId(QueryedPlayer.getUserId());
        gameActionBody.setMessage(message);
        gameActionBody.setRoom(roomService.getRoomAndPlayersById(player.getRoomId()));
        messageService.save(message);
        messagingTemplate.convertAndSend("/topic/room/" + player.getRoomId() + "/ALL", gameActionBody);
        return Result.success(player.getName());
    }

    @PostMapping("/destoryRoom")
    public Result destoryRoom(@RequestBody Room room) {
        Room getedRoom = roomService.getRoomAndPlayersById(room.getRoomId());
        getedRoom.setRoomState("DISCARDED");
        roomService.update(getedRoom);
        GameActionBody gameActionBody = new GameActionBody(ConstConfig.ROOM_DISCARDED, getedRoom.getRoomId());
        //构造actionbody的Message
        Message message = Message.system(getedRoom.getRoomId(),  "房间已废弃。",redisService,true);
        message.setMessageIndex(redisService.getNextMessageIndexAndIncr(String.valueOf(message.getRoomId())));
        gameActionBody.setMessage(message);
        gameActionBody.setRoom(roomService.getRoomAndPlayersById(getedRoom.getRoomId()));
        messageService.save(message);
        messagingTemplate.convertAndSend("/topic/room/" + getedRoom.getRoomId() + "/ALL", gameActionBody);
        return Result.success("已废村","已废村");

    }

    @PostMapping("/kickPlayer")
    public Result kickPlayer(@RequestBody Player player) {
        try {
            Player getedPlayer = playerService.QueryByRoomPlayerIdAndRoomId(player.getRoomPlayerId(), player.getRoomId());
            playerService.removeById(getedPlayer.getPlayerId());
            GameActionBody gameActionBody = new GameActionBody(ConstConfig.ROOM_PLAYER_KICKED, getedPlayer.getRoomId());
            //构造actionbody的Message
            Message message = Message.system(getedPlayer.getRoomId(),  getedPlayer.getName() + "被踢出了房间。",redisService,true);
            message.setMessageIndex(redisService.getNextMessageIndexAndIncr(String.valueOf(message.getRoomId())));
            gameActionBody.setMessage(message);
            gameActionBody.setRoom(roomService.getRoomAndPlayersById(getedPlayer.getRoomId()));
            messageService.save(message);
            messagingTemplate.convertAndSend("/topic/room/" + getedPlayer.getRoomId() + "/ALL", gameActionBody);
        } catch(Exception e){
            e.printStackTrace();
            return Result.error("踢出失败！","踢出失败！");
        }
        return Result.success("踢出成功","踢出成功");

    }

    @PostMapping("/changeGameSettings")
    public Result changeGameSettings(@RequestBody GameSettings gameSettings, @RequestParam int roomId) {
        gameSettings.setGameSettingId(roomService.getRoomAndPlayersById(roomId).getGameSettingId());
        try {
            gameSettingsService.update(gameSettings);
            //构造actionbody的Message
            Message message = Message.system(roomId, "房间设置已更新。",redisService,true);
            messageService.save(message);
            Room room = roomService.getRoomAndPlayersById(roomId);
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/ALL", new GameActionBody(ConstConfig.ROOM_GAMESETTINGS_CHANGE, message, room));
            return Result.success("成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("失败");
    }

    @PostMapping("/changeChannels")
    public Result changeChannels(@RequestBody Player player) {
        try {
            Player QueryedPlayer = playerService.QueryByUserIdAndRoomId(player.getUserId(), player.getRoomId());
            return Result.success(QueryedPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("失败");
    }

    @PostMapping("/ready")
    public Result changeGameSettings(@RequestBody Player player) {
        // 调用 update 方法
        int updated = playerService.updateByUserIdAndRoomId(player);


        if(updated != 0){
            Room room = roomService.getRoomAndPlayersById(player.getRoomId());
            messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId() + "/ALL", new GameActionBody(ConstConfig.ROOM_SOMEONE_READY,room));

            return Result.success("成功");
        }else{
            return Result.error("失败");
        }
//        playerService.update(player);

    }

}
