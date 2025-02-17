package com.jinrou.jinrouwerewolf.controller;

import com.jinrou.jinrouwerewolf.entity.*;
import com.jinrou.jinrouwerewolf.entity.Game.*;
import com.jinrou.jinrouwerewolf.service.Impl.GameService;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import com.jinrou.jinrouwerewolf.service.MessageService;
import com.jinrou.jinrouwerewolf.service.PlayerService;
import com.jinrou.jinrouwerewolf.service.RoomService;
import com.jinrou.jinrouwerewolf.service.UserService;
import com.jinrou.jinrouwerewolf.util.ConstConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: nanaya
 * @Date: 2024/07/22/1:00
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Spring 会自动注入

    @Autowired
    private GameService gameService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @Autowired
    private PlayerService playerService;
    private final ConcurrentHashMap<Integer, GameTask> gameTasks = new ConcurrentHashMap<>();
    private final ExecutorService gameExecutor = Executors.newFixedThreadPool(30);  // 创建一个固定大小的线程池


    @PostMapping("/startGame")
    public synchronized Result startGame(@RequestBody Room room) {
        Room getedRoom = roomService.getRoomAndPlayersById(room.getRoomId());
        for (Player player : getedRoom.getPlayers()) {
            if (!player.getIsReady()) {
                // 如果有玩家未准备,则开始失败
                return Result.error("某些玩家未准备，无法开始游戏", "某些玩家未准备，无法开始游戏");
            }
        }
        if (gameTasks.get(room.getRoomId()) != null) {
            return Result.error("游戏已经开始", "游戏已经开始");
        }

        GameInfo gameInfo = gameService.initGame(getedRoom);
        GameTask gameTask = null;
        if(gameInfo != null){
            try {
                //实例化 GameTask
                gameTask = new GameTask(getedRoom, gameInfo, messagingTemplate, this, simpUserRegistry, redisService, messageService,roomService,playerService);
                gameExecutor.submit(gameTask); // 线程池管理任务
                gameTasks.put(getedRoom.getRoomId(), gameTask);
            } catch (Exception e) {
                return Result.error("游戏开始失败", "游戏开始失败");
            }
        }else{
            return Result.error("失败，请检查配置合理性", "失败，请检查配置合理性");
        }

        return Result.success("成功", "游戏开始");
    }


    public GameTask getGameTask(Integer roomId) {
        return gameTasks.get(roomId);
    }

    // 游戏结束时移除任务
    public void endGame(Integer roomId) {
        GameTask gameTask = gameTasks.get(roomId);
        if (gameTask != null) {
            gameTasks.remove(roomId);  // 移除任务
        }

    }
}
