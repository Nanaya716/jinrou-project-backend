package com.jinrou.jinrouwerewolf;

import com.jinrou.jinrouwerewolf.controller.GameTask;
import com.jinrou.jinrouwerewolf.entity.Game.*;
import com.jinrou.jinrouwerewolf.entity.Identity.Murabito;
import com.jinrou.jinrouwerewolf.entity.Identity.Werewolf;
import com.jinrou.jinrouwerewolf.service.Impl.GameService;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: nanaya
 * @Date: 2024/07/08/7:36
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@SpringBootTest
@Slf4j
//@RunWith(SpringRunner.class)
public class GameThreadTest {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Spring 会自动注入

    @Autowired
    private GameService gameService;

    @Test
    public void gameThreadTest() {
        // 1. 模拟房间和游戏数据
//        Room room = createTestRoom();
//        GameInfo gameInfo = gameService.initGame(room);
//        System.out.println(room);
//        System.out.println(gameInfo.toString());
//
//        // 2. 创建一个模拟的 SimpMessagingTemplate
//        SimpMessagingTemplate mockedTemplate = Mockito.mock(SimpMessagingTemplate.class);
//
//        // 3. 实例化 GameTask
//        GameTask gameTask = new GameTask(room, gameInfo, mockedTemplate);
//        // 4. 在单独的线程中运行 GameTask
//        Thread gameThread = new Thread(gameTask);
//        gameThread.start();
//
//        // 5. 等待线程结束（模拟完整的游戏逻辑）
//        try {
//            gameThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private Room createTestRoom() {
        Room room = new Room();
        room.setRoomId(1);

        // 创建测试玩家
        List<Player> players = new ArrayList<>();
        // 模拟玩家
        for (int i = 1; i <= 5; i++) {
            Player player = new Player();
            player.setName("玩家" + i);
            player.setRoomPlayerId(i);
            player.setIsAlive(true);
            player.setIsReady(true);
            player.setUserId(i);
            players.add(player);
        }

        room.setPlayers(players);

        // 设置游戏配置
        GameSettings settings = new GameSettings();
        settings.setDayDuration(5); // 10 秒白天
        settings.setNightDuration(5); // 10 秒夜晚
        settings.setVoteDuration(5); // 5 秒投票

        List<String> identityList = new ArrayList<>();
        identityList.add("人狼");
        settings.setIdentityList(identityList);

        room.setGameSettings(settings);
        return room;
    }

    private GameInfo createTestGameInfo(Room room) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setRoomId(room.getRoomId());
        gameInfo.setGameState("DAY"); // 初始状态为白天
        gameInfo.setGameSettings(room.getGameSettings());
        return gameInfo;
    }
}
