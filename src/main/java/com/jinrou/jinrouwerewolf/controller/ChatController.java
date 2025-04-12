package com.jinrou.jinrouwerewolf.controller;

import com.alibaba.fastjson.JSON;
import com.jinrou.jinrouwerewolf.entity.Game.GameActionBody;
import com.jinrou.jinrouwerewolf.entity.Game.Message;
import com.jinrou.jinrouwerewolf.entity.Game.Player;
import com.jinrou.jinrouwerewolf.entity.Game.Room;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.entity.WebSocketSessionStore;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import com.jinrou.jinrouwerewolf.service.MessageService;
import com.jinrou.jinrouwerewolf.service.RoomService;
import com.jinrou.jinrouwerewolf.util.ConstConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: nanaya
 * @Date: 2024/07/25/11:03
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Controller
public class ChatController {
    @Autowired
    private SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RoomService roomService;
    private final GameController gameController;

    public ChatController(SimpMessagingTemplate messagingTemplate, GameController gameController) {
        this.messagingTemplate = messagingTemplate;
        this.gameController = gameController;
    }

    /**
     * 休息室
     * 收到消息时反馈推送给所有人
     *
     * @param message
     */
    @MessageMapping("/chatRoom")
    public void chatRoomSendMessage(Message message) {
        message.setMessageIndex(redisService.getNextMessageIndexAndIncr(String.valueOf(message.getRoomId())));
        messageService.save(message);
        messagingTemplate.convertAndSend("/topic/chatRoom", message);
    }

    /**
     * 休息室
     * 客户端订阅时返回当前消息
     *
     * @param headerAccessor
     */
    @SubscribeMapping("/chatRoom")
    public void chatRoomSubscribeMessage(SimpMessageHeaderAccessor headerAccessor) {
        // 模拟返回初始化数据（历史消息）
        Integer userId = WebSocketSessionStore.getWebsocketSessions(headerAccessor.getSessionId()).getUser().getUserId();
        String userId2 = (String) headerAccessor.getSessionAttributes().get("userId");
        List<Message> msgs = messageService.loadMessagesByRoomIdAndLimit("0", 100);
//        for (SimpUser user : simpUserRegistry.getUsers()) {
//            System.out.println("User: " + user.getName() + ", Sessions: " + user.getSessions());
//        }
        messagingTemplate.convertAndSendToUser(
                userId2,              // 前端 WebSocket 的 sessionId
                "/queue/" + 0 + "/messages",       // 匹配的队列路径
                msgs                 // 要发送的消息内容
        );
        OnlineUserUpdate("/topic/chatRoom");
    }

    /**
     * 休息室
     * 在线人数更新
     *
     * @param destination
     */
    public void OnlineUserUpdate(String destination) {
        //获取在线用户
        List<User> users = WebSocketSessionStore.getUsersByDestination(destination);
        // 模拟更新当前在线用户
        Message sendToAllUserMsg = new Message();
        sendToAllUserMsg.setMessageType("GM");
        sendToAllUserMsg.setMessageContent(JSON.toJSONString(users));
        //更新在线用户列表
        messagingTemplate.convertAndSend(destination, sendToAllUserMsg);
    }

    /**
     * 普通房间
     * 收到消息时反馈推送给所有人
     *
     * @param message
     */
    @MessageMapping("/room/{roomId}/{nowSendChannel}")
    public void RoomSendMessage(@DestinationVariable String roomId, Message message, @DestinationVariable String nowSendChannel, SimpMessageHeaderAccessor headerAccessor) {
        message.setMessageIndex(redisService.getNextMessageIndexAndIncr(String.valueOf(message.getRoomId())));
        messageService.save(message);
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        GameTask gameTask = gameController.getGameTask(Integer.valueOf(roomId));

        switch (nowSendChannel) {
            case "ALL":
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/ALL", message);
                break;
            case "DAYTALK":
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/DAYTALK", message);
                break;
            case "WOLFTALK":
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/WOLFTALK", message);
                break;
            case "KYOUYUUTALK":
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/KYOUYUUTALK", message);
                break;
            case "KITSUNETALK":
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/KITSUNETALK", message);
                break;
            case "WATCHTALK":
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/WATCHTALK", message);
                break;
            case "UNDERTALK":
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/UNDERTALK", message);
                break;
            case "SELFTALK":
                messagingTemplate.convertAndSendToUser(
                        userId,              // 前端 WebSocket 的 sessionId
                        "/queue/" + roomId + "/messages",       // 匹配的队列路径
                        message                 // 要发送的消息内容
                );
                Integer GmUserId = gameTask.getRoom().getPlayers().stream().filter(player -> player.getIdentity().getName() == "GM").findFirst().orElse(new Player()).getUserId();  // 获取第一个匹配的玩家
                messagingTemplate.convertAndSendToUser(
                        GmUserId.toString(),              // 前端 WebSocket 的 sessionId
                        "/queue/" + roomId + "/messages",       // 匹配的队列路径
                        message                 // 要发送的消息内容
                );
                break;
            default:
                //私聊
                messagingTemplate.convertAndSendToUser(
                        userId,              // 前端 WebSocket 的 sessionId
                        "/queue/" + roomId + "/messages",       // 匹配的队列路径
                        message                 // 要发送的消息内容
                );
                Integer GmToUserId = gameTask.getRoom().getPlayers().stream().filter(player -> player.getRoomPlayerId().equals(message.getMessageGmToPlayerId()))  // 过滤出满足条件的玩家
                    .findFirst().orElse(new Player()).getUserId();  // 获取第一个匹配的玩家
                messagingTemplate.convertAndSendToUser(
                        GmToUserId.toString(),              // 前端 WebSocket 的 sessionId
                        "/queue/" + roomId + "/messages",       // 匹配的队列路径
                        message                 // 要发送的消息内容
                );
        }
    }


    @MessageMapping("/room/{roomId}/{nowSendChannel}/gameAction")
    public void doGameAction(@DestinationVariable String roomId, GameActionBody gameActionBody, @DestinationVariable String nowSendChannel, SimpMessageHeaderAccessor headerAccessor) {
        GameTask gameTask = gameController.getGameTask(Integer.valueOf(roomId));
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        Integer GMuserId = gameTask.getRoom().getPlayers().stream().filter(player -> player.getIdentity().getName() == "GM").findFirst().orElse(new Player()).getUserId();
        switch (gameActionBody.getCode()) {
            case ConstConfig.GAME_ACTION_URANAI:
                gameTask.getGameInfo().getUranaiMap().put(gameActionBody.getOperatorOfPlayerId(), gameActionBody.getTargetOfPlayerId());
                String result = null;
                if (gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getTargetOfPlayerId()).getIdentity().getName() == "人狼") {
                    result = "●";
                } else {
                    result = "○";
                }
                GameActionBody uranaiResult = gameTask.creatStartedGameActionBody(ConstConfig.GAME_URANAI_RESULT, null, null,
                        Message.system(Integer.valueOf(roomId), gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getOperatorOfPlayerId()).getName() + " 占卜了 " + gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getTargetOfPlayerId()).getName() + " 的身份，结果为" + result, redisService, true, gameActionBody.getOperatorOfPlayerId()));
                gameTask.saveAndNotifyOnePlayer(uranaiResult, Integer.valueOf(userId));
                //通知GM
                gameTask.notifyOnePlayer(uranaiResult, GMuserId);
                break;
            case ConstConfig.GAME_ACTION_VOTE:
                gameTask.getGameInfo().getVoteInfo().put(gameActionBody.getOperatorOfPlayerId(), gameActionBody.getTargetOfPlayerId());
                GameActionBody voteResult = gameTask.creatStartedGameActionBody(ConstConfig.GAME_URANAI_RESULT, null, null,
                        Message.system(Integer.valueOf(roomId), gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getOperatorOfPlayerId()).getName() + " 向 " + gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getTargetOfPlayerId()).getName() + " 投票了。", redisService, true, gameActionBody.getOperatorOfPlayerId()));
                gameTask.saveAndNotifyOnePlayer(voteResult, Integer.valueOf(userId));
                gameTask.notifyOnePlayer(voteResult, GMuserId);
                break;
            case ConstConfig.GAME_ACTION_KAMI:
                gameTask.getGameInfo().getKillMap().put(gameActionBody.getOperatorOfPlayerId(), gameActionBody.getTargetOfPlayerId());
                GameActionBody hasKamiResult = gameTask.creatStartedGameActionBody(ConstConfig.GAME_WOLF_HAS_KAMI, null, null,
                        Message.wolftalk(Integer.valueOf(roomId), gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getOperatorOfPlayerId()).getName() + " 袭击了 " + gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getTargetOfPlayerId()).getName() + "。", redisService, true, null));
                gameTask.saveAndNotifyPlayers(hasKamiResult, "/WOLFTALK");

                break;
            case ConstConfig.GAME_ACTION_GM_TIME_LONG:
                gameTask.modifyRemainingTime(20000);
                GameActionBody gameTimeLongBody = gameTask.creatStartedGameActionBody(ConstConfig.GAME_ACTION_GM_TIME_LONG, null, null,
                        Message.system(Integer.valueOf(roomId), null, redisService, false, null));
                gameTask.NotifyPlayers(gameTimeLongBody, "/ALL");
                break;
            case ConstConfig.GAME_ACTION_KARIUDO:
                gameTask.getGameInfo().getKariudoMap().put(gameActionBody.getOperatorOfPlayerId(), gameActionBody.getTargetOfPlayerId());
                GameActionBody kariudoResult = gameTask.creatStartedGameActionBody(ConstConfig.GAME_KARIUDO_RESULT, null, null,
                        Message.system(Integer.valueOf(roomId), gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getOperatorOfPlayerId()).getName() + " 守护了 " + gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getTargetOfPlayerId()).getName() + "。", redisService, true, gameActionBody.getOperatorOfPlayerId()));
                gameTask.saveAndNotifyOnePlayer(kariudoResult, Integer.valueOf(userId));
                gameTask.notifyOnePlayer(kariudoResult, GMuserId);
                break;
            case ConstConfig.GAME_ACTION_GM_TIME_SHORT:
                if (gameTask.getPhaseTimer().getRemainingTime() > 20000) {
                    gameTask.modifyRemainingTime(-20000);
                }
                GameActionBody gameTimeShortBody = gameTask.creatStartedGameActionBody(ConstConfig.GAME_ACTION_GM_TIME_SHORT, null, null,
                        Message.system(Integer.valueOf(roomId), null, redisService, false, null));
                gameTask.NotifyPlayers(gameTimeShortBody, "/ALL");
                break;
            case ConstConfig.GAME_GM_KILL:
                gameTask.GmKill(gameActionBody.getTargetOfPlayerId());
                GameActionBody gmKillActionBody = gameTask.creatStartedGameActionBody(ConstConfig.GAME_GM_KILL, null, null,
                        Message.system(Integer.valueOf(roomId),  gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getTargetOfPlayerId()).getName() + " 被GM处死了。", redisService, true, null));
                gameTask.saveAndNotifyPlayers(gmKillActionBody, "/ALL");
                break;
            case ConstConfig.GAME_GM_REVIVE:
                gameTask.GmRevive(gameActionBody.getTargetOfPlayerId());
                GameActionBody gmReviveActionBody = gameTask.creatStartedGameActionBody(ConstConfig.GAME_GM_REVIVE, null, null,
                        Message.system(Integer.valueOf(roomId),  gameTask.getRoom().getPlayerByRoomPlayerId(gameActionBody.getTargetOfPlayerId()).getName() + " 被GM复活了。", redisService, true, null));
                gameTask.saveAndNotifyPlayers(gmReviveActionBody, "/ALL");
                break;
        }
    }

    /**
     * 普通房间
     * 客户端订阅时返回当前消息
     *
     * @param headerAccessor
     */
    @SubscribeMapping("/room/{roomId}")
    public void RoomSubscribeMessage(@DestinationVariable Integer roomId,
                                     SimpMessageHeaderAccessor headerAccessor) {
        // 获取当前userId
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        //准备gameActionBody，用于返回给前端，初始化游戏状态
        GameActionBody gameActionBody = new GameActionBody(ConstConfig.ROOM_INITIAL, roomId);
        Room room = roomService.getRoomAndPlayersById(roomId);

        GameTask gameTask = gameController.getGameTask(roomId);
        gameActionBody.setRoom(room);
        System.out.println(room);
        List<Message> msgs = null;
        if (gameTask != null) {
            gameActionBody.setGameStarted(true);

            // 游戏正在进行中
            gameActionBody.setDayOrNightOrVote(gameTask.getGameInfo().getGameState());
            gameActionBody.setDayNum(gameTask.getGameInfo().getDayCount());
            gameActionBody.setSeconds(gameTask.getPhaseTimer().getRemainingTime() / 1000);
            Player player = gameTask.getRoom().getPlayerByUserId(Integer.valueOf(userId));
            List<String> channels = new ArrayList<>();
            /**
             * ALL-全体消息
             * SYSTEM-系统消息
             * DAYTALK-白天正常讨论
             * WOLFTALK-狼频夜晚讨论
             * KYOUYUUTALK-共有夜晚讨论
             * KITSUNETALK-妖狐夜晚讨论
             * WATCHTALK-观战讨论
             * SELFTALK-自言自语
             * UNDERTALK-灵界讨论
             */
            switch (player.getIdentity().getName()) {
                case "人狼":
                case "听狂人":
                    channels = List.of("ALL", "DAYTALK", "SYSTEM", "WOLFTALK","SELFTALK");
                    break;
                case "村人":
                case "占卜师":
                case "灵能者":
                case "猎人":
                case "狂人":
                case "狂信者":
                case "猫又":
                case "背德者":
                    channels = List.of("ALL", "DAYTALK", "SYSTEM","SELFTALK");
                    break;
                case "共有者":
                    channels = List.of("ALL", "DAYTALK", "SYSTEM", "KYOUYUUTALK","SELFTALK");
                    break;
                case "妖狐":
                    channels = List.of("ALL", "DAYTALK", "SYSTEM", "KITSUNETALK","SELFTALK");
                    break;
                case "GM":
                    channels = List.of("ALL", "DAYTALK", "SYSTEM", "WOLFTALK", "KYOUYUUTALK", "KITSUNETALK", "WATCHTALK","SELFTALK");
            }
            if(player.getIdentity().getName() == "GM"){
                msgs = messageService.loadMessagesByRoomId(roomId);
            }else{
                msgs = messageService.loadMessagesByRoomIdAndRoomPlayerIdAndChannel(roomId, player.getRoomPlayerId(), channels ,player.getUserId());
            }

            if (player.getIdentity().getName() == "GM") {
                gameActionBody.setRoom(gameTask.getRoom());
            } else {
                gameActionBody.setRoom(gameTask.getRoom().PlayersWithIdentityNull());
            }

            if (player.getIdentity().getName().equals("人狼")) {
                if(gameTask.getGameInfo().getDayCount() != 1 && gameTask.getGameInfo().getKillMap().size() == 0
                        && (gameTask.getGameInfo().getGameState() == "NIGHT" || gameTask.getGameInfo().getGameState() == "MORNING"
                        && player.getIsAlive())){
                    gameActionBody.setNeedKami(true);
                }
                List<Integer> partners = gameTask.countIdentity("人狼").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList()); // 收集为 List<Integer>
                gameActionBody.setPartners(partners);
            }
            if (player.getIdentity().getName().equals("狂信者")) {
                List<Integer> partners = gameTask.countIdentity("人狼").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList()); // 收集为 List<Integer>
                gameActionBody.setPartners(partners);
            }
            if (player.getIdentity().getName().equals("共有者")) {
                List<Integer> partners = gameTask.countIdentity("共有者").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList()); // 收集为 List<Integer>
                gameActionBody.setPartners(partners);
            }
            if (player.getIdentity().getName().equals("妖狐")) {
                List<Integer> partners = gameTask.countIdentity("妖狐").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList()); // 收集为 List<Integer>
                gameActionBody.setPartners(partners);
            }
            if (player.getIdentity().getName().equals("背德者")) {
                List<Integer> partners = gameTask.countIdentity("妖狐").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList()); // 收集为 List<Integer>
                gameActionBody.setPartners(partners);
            }
            if ((!player.getIdentity().getName().equals("GM"))
                    && player.getIsAlive()
                    && gameTask.getGameInfo().getVoteInfo().get(player.getRoomPlayerId()) == null
                    && (gameTask.getGameInfo().getGameState() == "VOTING" || gameTask.getGameInfo().getGameState() == "SUSPEND")) {
                gameActionBody.setNeedVote(true);
            }
            if (player.getIdentity().getName().equals("占卜师")
                    && player.getIsAlive()
                    && (gameTask.getGameInfo().getGameState() == "NIGHT" || gameTask.getGameInfo().getGameState() == "MORNING")
            && gameTask.getGameInfo().getUranaiMap().get(player.getRoomPlayerId()) == null) {
                gameActionBody.setNeedUranai(true);
            }
            if (player.getIdentity().getName().equals("猎人")
                    && player.getIsAlive()
                    && (gameTask.getGameInfo().getGameState() == "NIGHT" || gameTask.getGameInfo().getGameState() == "MORNING")
                    && gameTask.getGameInfo().getDayCount() != 1
                    && gameTask.getGameInfo().getKariudoMap().get(player.getRoomPlayerId()) == null) {
                    gameActionBody.setNeedKariudo(true);
            }

            if (gameTask.getRoom().getPlayerByUserId(Integer.parseInt(userId)) != null) {
                gameActionBody.setIdentityName(gameTask.getRoom().getPlayerByUserId(Integer.parseInt(userId)).getIdentity().getName());
            } else {
                gameActionBody.setIdentityName("观战者");
            }

        } else {

            msgs = messageService.loadMessagesByRoomId(roomId);
            gameActionBody.setGameStarted(false);
            if ("ENDED".equals(room.getRoomState()) || "DISCARDED".equals(room.getRoomState())) {
                String identityName = null;
                if(room.getPlayerByUserId(Integer.valueOf(userId)) != null){
                    if(room.getPlayerByUserId(Integer.valueOf(userId)).getIdentity() != null){
                        identityName = room.getPlayerByUserId(Integer.valueOf(userId)).getIdentity().getName();
                        gameActionBody.setIdentityName(room.getPlayerByUserId(Integer.valueOf(userId)).getIdentity().getName());
                    }
                }else{
                    //该玩家不在房间内
                    if(!"DISCARDED".equals(room.getRoomState())){
                        gameActionBody.setIdentityName("观战者");
                    }
                }
            }
        }

        /**
         * 构建并发送消息列表
         */


        messagingTemplate.convertAndSendToUser(
                userId,              // 用户标识
                "/queue/" + roomId + "/messages",       // 匹配的队列路径
                msgs                 // 要发送的消息内容
        );
        /**
         * 通信当前游戏状态
         */
        messagingTemplate.convertAndSendToUser(
                userId,              // 用户标识
                "/queue/" + roomId + "/messages",       // 匹配的队列路径
                gameActionBody);               // 要发送的消息内容
    }

    @MessageMapping("/default")
    public void handleChatMessage2(Message message) {
        messagingTemplate.convertAndSend("/topic/default", message.getMessageContent());
    }


}
