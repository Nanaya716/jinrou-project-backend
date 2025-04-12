package com.jinrou.jinrouwerewolf.controller;

import com.jinrou.jinrouwerewolf.entity.Game.*;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import com.jinrou.jinrouwerewolf.service.MessageService;
import com.jinrou.jinrouwerewolf.service.PlayerService;
import com.jinrou.jinrouwerewolf.service.RoomService;
import com.jinrou.jinrouwerewolf.util.ConstConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Author: nanaya
 * @Date: 2024/08/27/10:53
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */

@Getter
@Setter
public class GameTask implements Runnable {
    private Room room;
    private GameInfo gameInfo;
    private PhaseTimer phaseTimer; // 添加一个PhaseTimer实例
    private final SimpMessagingTemplate messagingTemplate;
    private GameController gameController;
    private SimpUserRegistry simpUserRegistry;
    private RedisService redisService;
    private MessageService messageService;
    private RoomService roomService;
    private PlayerService playerService;

    public GameTask(Room room, GameInfo gameInfo,
                    SimpMessagingTemplate messagingTemplate,
                    GameController gameController,
                    SimpUserRegistry simpUserRegistry,
                    RedisService redisService,
                    MessageService messageService,
                    RoomService roomService,
                    PlayerService playerService) {
        this.room = room;
        this.gameInfo = gameInfo;
        this.messagingTemplate = messagingTemplate;
        this.gameController = gameController;
        this.simpUserRegistry = simpUserRegistry;
        this.redisService = redisService;
        this.messageService = messageService;
        this.roomService = roomService;
        this.playerService = playerService;
    }

    /**
     * 游戏主线程的入口，控制游戏的生命周期。
     * 包括处理白天和夜晚的循环逻辑，直到游戏结束。
     */
    @Override
    public void run() {
        boolean isHasBeenNight = false;
        boolean ended = false;
        notifyClientStart();
        try {
            while (!ended) {
                //判断是否经历过夜晚了，如果是，则增加天数计数
                if (isHasBeenNight) {
                    gameInfo.incrementDayCount(); // 增加天数计数
                    isHasBeenNight = false;
                }

                switch (gameInfo.getGameState()) {
                    case "SILENT":
                        ended = handleSilentPhase(); // 静默阶段处理
                        break;
                    case "DAY":
                        ended = handleDayPhase(); // 白天阶段处理
                        break;
                    case "NIGHT":
                        ended = handleNightPhase(); // 夜晚阶段处理
                        isHasBeenNight = true;
                        break;
                    case "VOTING":
                        //平局处理
                        break;
                    default:
                        return;
                }
            }
            // 宣布游戏胜利结果
            announceWinner();
            endInitial();
            gameController.endGame(room.getRoomId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理白天阶段的逻辑。
     * 包括讨论阶段和投票阶段。
     *
     * @return 游戏是否结束
     */
    private boolean handleDayPhase() {
        gameInfo.setGameState("DAY");
        phaseTimer = new PhaseTimer(gameInfo.getGameSettings().getDayDuration() * 1000L,
                this.room.getRoomId(),
                this.gameController);
        GameActionBody DayComingGameActionBody = creatStartedGameActionBody(ConstConfig.GAME_STATUS_DAY,
                null,
                null,
                Message.system(this.room.getRoomId(), "第" + getGameInfo().getDayCount() + "天的白天到来了。", redisService, true)
        );

        phaseTimer.start(
                () -> {
                }, false, DayComingGameActionBody);
        // 等待白天阶段结束
        try {
            // 等待 PhaseTimer 计时结束
            phaseTimer.awaitCompletion(); // 这里会等待计时器结束
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return handleVotingPhase(); // 处理投票阶段
    }

    private boolean handleSilentPhase() {
        gameInfo.setGameState("SILENT");
        phaseTimer = new PhaseTimer(gameInfo.getGameSettings().getNSecondRule() * 1000L,
                this.room.getRoomId(),
                this.gameController);
        GameActionBody SilentComingGameActionBody = creatStartedGameActionBody(ConstConfig.GAME_STATUS_SILENT,
                null,
                null,
                Message.system(this.room.getRoomId(), null, redisService, false)
        );

        phaseTimer.start(
                () -> {
                }, false, SilentComingGameActionBody);
        // 等待静默阶段结束
        try {
            // 等待 PhaseTimer 计时结束
            phaseTimer.awaitCompletion(); // 这里会等待计时器结束
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        gameInfo.setGameState("DAY");
        return false; // 处理投票阶段
    }

    /**
     * 处理投票阶段的逻辑。
     *
     * @return 游戏是否结束
     */
    private boolean handleVotingPhase() {
        // 进入投票阶段
        gameInfo.setGameState("VOTING");
        phaseTimer = new PhaseTimer(gameInfo.getGameSettings().getVoteDuration() * 1000L,
                this.room.getRoomId(),
                this.gameController);
        GameActionBody VoteComingGameActionBody = creatStartedGameActionBody(ConstConfig.GAME_STATUS_VOTE,
                null,
                null,
                Message.system(this.room.getRoomId(), "讨论时间已经结束，请选择投票对象。", redisService, true)
        );
        //平票循环
        for (int i = 1; i <= 4; i++) {

            int current = i;
            phaseTimer = new PhaseTimer(gameInfo.getGameSettings().getVoteDuration() * 1000L,
                    this.room.getRoomId(),
                    this.gameController);
            if (i == 1) {
                phaseTimer.start(
                        () -> {
                            // 模拟投票操作
                        }, true, VoteComingGameActionBody);
            } else {
                phaseTimer.start(
                        () -> {
                            // 模拟投票操作
                        }, true, creatStartedGameActionBody(ConstConfig.GAME_STATUS_NOT_OVER_TIE,
                                null,
                                null,
                                Message.system(this.room.getRoomId(), "平票！第" + (i - 1) + "次平票，平票4次作平局处理。", redisService, true)));
            }


            // 等待投票结束
            try {
                // 等待 PhaseTimer 计时结束
                phaseTimer.awaitCompletion(); // 这里会等待计时器结束
                //判断是否需要进入投票延长阶段
                List<Player> filteredPlayers = this.getRoom().getPlayers().stream()
                        .filter(player -> player.getRoomPlayerId() != 0 && player.getIsAlive()) // 过滤掉 roomPlayerId == 0 的玩家
                        .toList();
                if (this.room.getGameSettings().isGmMode() && gameInfo.isGmInRoom()) {
                    if (this.getGameInfo().getVoteInfo().size() < filteredPlayers.size() - 1) {
                        //进入延长阶段
                        this.gameInfo.setGameState("SUSPEND");
                        phaseTimer = new PhaseTimer(gameInfo.getGameSettings().getMorningDuration() * 1000L,
                                this.room.getRoomId(),
                                this.gameController);
                        GameActionBody SuspendGameActionBody = creatStartedGameActionBody(ConstConfig.GAME_SUSPEND_TIME,
                                null,
                                null,
                                Message.system(this.room.getRoomId(), "迎来黄昏时间，请尽快投票。", redisService, true)
                        );
                        phaseTimer.start(
                                () -> {
                                    //TODO-未操作的猝死
                                }, true, SuspendGameActionBody);
                        phaseTimer.awaitCompletion(); // 这里会等待计时器结束
                    }
                } else {
                    if (this.getGameInfo().getVoteInfo().size() < filteredPlayers.size() - 1) {
                        //进入延长阶段
                        this.gameInfo.setGameState("SUSPEND");
                        phaseTimer = new PhaseTimer(gameInfo.getGameSettings().getMorningDuration() * 1000L,
                                this.room.getRoomId(),
                                this.gameController);
                        GameActionBody SuspendGameActionBody = creatStartedGameActionBody(ConstConfig.GAME_SUSPEND_TIME,
                                null,
                                null,
                                Message.system(this.room.getRoomId(), "迎来黄昏时间，请尽快投票。", redisService, true)
                        );
                        phaseTimer.start(
                                () -> {
                                    //TODO-未操作的猝死
                                }, true, SuspendGameActionBody);
                        phaseTimer.awaitCompletion(); // 这里会等待计时器结束
                        gameInfo.setGameState("VOTING");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 处理投票结果
            String state = null;
            Integer votedRoomPlayerId = processVoteResults();  //处刑逻辑在这里面
            if (votedRoomPlayerId != -1) {
                state = isGameOver();
            } else {
                state = ConstConfig.GAME_STATUS_NOT_OVER_TIE;
            }

            // 发送投票详情
            saveAndNotifyPlayers(creatStartedGameActionBody(ConstConfig.GAME_VOTE_INFO_BODY,
                    null,
                    null,
                    Message.system(this.room.getRoomId(), rebuildVoteInfoJsonString(), redisService, true)), "/ALL");
            gameInfo.getVoteInfo().clear();
            switch (state) {
                case ConstConfig.GAME_STATUS_NOT_END:
                    // 发送具体被投出的人
                    saveAndNotifyPlayers(creatStartedGameActionBody(ConstConfig.GAME_VOTE_INFO_RESULT,
                            null,
                            null,
                            Message.system(this.room.getRoomId(), this.room.getPlayerByRoomPlayerId(Integer.valueOf(votedRoomPlayerId)).getName() + "被处刑了。", redisService, true)), "/ALL");

                    if (getPlayerByRoomPlayerId(room.getPlayers(), votedRoomPlayerId).getIdentity().getName() == "猫又") {
                        votedNekomata();
                    }
                    String result = null;
                    if (this.room.getPlayerByRoomPlayerId(Integer.valueOf(votedRoomPlayerId)).getIdentity().getName() == "人狼") {
                        result = "●";
                    } else {
                        result = "○";
                    }
                    GameActionBody ReibaiGameActionBody = creatStartedGameActionBody(ConstConfig.GAME_STATUS_NIGHT,
                            null,
                            null,
                            Message.system(this.room.getRoomId(), "根据灵能结果， " + this.room.getPlayerByRoomPlayerId(Integer.valueOf(votedRoomPlayerId)).getName() + " 的结果为：" + result, redisService, true)
                    );
                    //通知灵能者处刑结果
                    this.countIdentity("灵能者").forEach(player -> saveAndNotifyOnePlayer(ReibaiGameActionBody, player.getUserId()));
                    this.gameInfo.setGameState("NIGHT");
                    return false;
                case ConstConfig.GAME_STATUS_NOT_OVER_TIE:
                    // 平票
                    if (i >= 4) {
                        //平局
                        return true;
                    }
                    continue;
                case ConstConfig.GAME_STATUS_OVER_MURABITO_WIN:
                case ConstConfig.GAME_STATUS_OVER_WEREWOLF_WIN:
                case ConstConfig.GAME_STATUS_OVER_KITSUNE_WIN:
                    // 游戏结束
                    return true;
                default:
                    // 其他情况
                    return false;
            }
        }

        return true;
    }

    /**
     * 处理夜晚阶段的逻辑。
     * 包括角色行动和检查游戏是否结束。
     *
     * @return 游戏是否结束
     */
    private boolean handleNightPhase() {
        gameInfo.setGameState("NIGHT");
        phaseTimer = new PhaseTimer(gameInfo.getGameSettings().getNightDuration() * 1000L,
                this.room.getRoomId(),
                this.gameController);
        GameActionBody NightComingGameActionBody = creatStartedGameActionBody(ConstConfig.GAME_STATUS_NIGHT,
                null,
                null,
                Message.system(this.room.getRoomId(), "第" + getGameInfo().getDayCount() + "天的夜晚到来了。", redisService, true)
        );
        phaseTimer.start(() -> {
        }, false, NightComingGameActionBody);

        // 等待夜晚时间结束
        try {
            // 等待晚上计时结束
            phaseTimer.awaitCompletion(); // 这里会等待计时器结束
            if (gameInfo.getDayCount() == 1) {
                //第一天的初日牺牲者
                this.getGameInfo().getKillMap().put(0, 0);
                if("占卜师".equals(getPlayerByRoomPlayerId(room.getPlayers(), 0).getIdentity().getName())){
                    this.getGameInfo().getUranaiMap().put(0, 0);
                }
            }
            //判断是否需要进入延长阶段
            if (this.getGameInfo().getUranaiMap().size() < this.countIdentity("占卜师").size() ||
                    (this.getGameInfo().getDayCount() != 1 && this.getGameInfo().getKariudoMap().size() < this.countIdentity("猎人").size()) ||
                    this.getGameInfo().getKillMap().size() == 0) {
                //进入延长阶段
                this.gameInfo.setGameState("MORNING");
                phaseTimer = new PhaseTimer(gameInfo.getGameSettings().getMorningDuration() * 1000L,
                        this.room.getRoomId(),
                        this.gameController);
                GameActionBody morningComingGameActionBody = creatStartedGameActionBody(ConstConfig.GAME_STATUS_MORNING,
                        null,
                        null,
                        Message.system(this.room.getRoomId(), "进入黎明时间，还未能进行操作的职业请尽快操作。", redisService, true)
                );
                phaseTimer.start(
                        () -> {
                            //TODO-未操作的猝死
                        }, true, morningComingGameActionBody);
                phaseTimer.awaitCompletion(); // 这里会等待计时器结束
            }
            //计算map中的结果然后死掉
            nightKill();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 处理夜晚结果
        String state = processNightResults();
        switch (state) {
            case ConstConfig.GAME_STATUS_NOT_END:
                if (gameInfo.getGameSettings().getNSecondRule() != 0) {
                    gameInfo.setGameState("SILENT");
                } else {
                    gameInfo.setGameState("DAY");
                }
                // 正常进行的分支
                return false;
            case ConstConfig.GAME_STATUS_OVER_MURABITO_WIN:
            case ConstConfig.GAME_STATUS_OVER_WEREWOLF_WIN:
            case ConstConfig.GAME_STATUS_OVER_KITSUNE_WIN:
                // 游戏结束
                return true;
            default:
                // 其他情况
                return true;
        }
    }

    /**
     * 通知玩家游戏的阶段变化或系统信息。
     *
     * @param topic 消息主题
     */

    public void saveAndNotifyPlayers(GameActionBody gameActionBody, String topic) {
        if (gameActionBody.getMessage() != null) {
            messageService.save(gameActionBody.getMessage());
        }
        messagingTemplate.convertAndSend("/topic/room/" + gameInfo.getRoomId() + topic, gameActionBody);
    }

    public void NotifyPlayers(GameActionBody gameActionBody, String topic) {
        messagingTemplate.convertAndSend("/topic/room/" + gameInfo.getRoomId() + topic, gameActionBody);
    }


    public void saveAndNotifyOnePlayer(GameActionBody gameActionBody, Integer userId) {
        if (gameActionBody.getMessage() != null) {
            messageService.save(gameActionBody.getMessage());
        }
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),              // 前端 WebSocket 的 sessionId
                "/queue/" + room.getRoomId() + "/messages",       // 匹配的队列路径
                gameActionBody                 // 要发送的消息内容
        );
    }

    public void notifyOnePlayer(GameActionBody gameActionBody, Integer userId) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),              // 前端 WebSocket 的 sessionId
                "/queue/" + room.getRoomId() + "/messages",       // 匹配的队列路径
                gameActionBody                 // 要发送的消息内容
        );
    }

    public GameActionBody creatStartedGameActionBody(String code,
                                                     Integer operatorOfPlayerId,
                                                     Integer targetOfPlayerId,
                                                     Message message) {
        GameActionBody gameActionBody = new GameActionBody();
        gameActionBody.setGameStarted(true);
        gameActionBody.setDayOrNightOrVote(this.gameInfo.getGameState());
        gameActionBody.setDayNum(this.gameInfo.getDayCount());
        gameActionBody.setSeconds(this.getPhaseTimer().getRemainingTime() / 1000);
        gameActionBody.setRoom(this.room.PlayersWithIdentityNull());
        gameActionBody.setCode(code);
        gameActionBody.setOperatorOfPlayerId(operatorOfPlayerId);
        gameActionBody.setTargetOfPlayerId(targetOfPlayerId);
        message.setDayTime(String.valueOf(getGameInfo().getDayCount()));
        gameActionBody.setMessage(message);
        gameActionBody.setRoomId(this.room.getRoomId());
        return gameActionBody;
    }

    /**
     * 处理投票结果。
     *
     * @return 游戏是否结束
     */
    private Integer processVoteResults() {
        int voteResult = gameInfo.getVoteResult();
        // 检查投票是否平局
        if (voteResult == -1) {
            return -1;
        }

        // 执行投票结果
        Player votedPlayer = getPlayerByRoomPlayerId(room.getPlayers(), voteResult);
        if (votedPlayer != null) {
            execute(voteResult);
            checkKitsuneDie();
        }
        return votedPlayer.getRoomPlayerId();
    }

    /**
     * 处理夜晚结果
     *
     * @return 游戏是否结束
     */
    private String processNightResults() {
        return isGameOver();
    }

    /**
     * 宣布游戏胜利者。
     */
    private void announceWinner() {
        getRoom().setRoomState("ENDED");
        String winner = null;
        switch (isGameOver()) {
            case ConstConfig.GAME_STATUS_OVER_MURABITO_WIN:
                saveAndNotifyPlayers(creatStartedGameActionBody(ConstConfig.GAME_STATUS_OVER_MURABITO_WIN,
                        null,
                        null,
                        Message.system(this.room.getRoomId(), "胜利结算！村人胜利！", redisService, true)
                ), "/ALL");
                winner = "村人";
                break;
            case ConstConfig.GAME_STATUS_OVER_WEREWOLF_WIN:
                saveAndNotifyPlayers(creatStartedGameActionBody(ConstConfig.GAME_STATUS_OVER_WEREWOLF_WIN,
                        null,
                        null,
                        Message.system(this.room.getRoomId(), "胜利结算！人狼胜利！", redisService, true)
                ), "/ALL");
                winner = "人狼";
                break;
            case ConstConfig.GAME_STATUS_OVER_KITSUNE_WIN:
                saveAndNotifyPlayers(creatStartedGameActionBody(ConstConfig.GAME_STATUS_OVER_KITSUNE_WIN,
                        null,
                        null,
                        Message.system(this.room.getRoomId(), "胜利结算！妖狐胜利！", redisService, true)
                ), "/ALL");
                winner = "妖狐";
                break;
            default:
                saveAndNotifyPlayers(creatStartedGameActionBody(ConstConfig.GAME_STATUS_OVER_TIE,
                        null,
                        null,
                        Message.system(this.room.getRoomId(), "平局。", redisService, true)
                ), "/ALL");
                winner = "平局";
                break;
        }
//        Message message = Message.system(this.getRoom().getRoomId(), "游戏结束。", redisService, true);
        GameActionBody gameEndgameActionBody = new GameActionBody(ConstConfig.GAME_STATUS_IS_OVER, null, this.getRoom());
        gameEndgameActionBody.setGameStarted(true);
        gameEndgameActionBody.setDayOrNightOrVote(null);
        saveAndNotifyPlayers(gameEndgameActionBody, "/ALL");
        getRoom().setWinner(winner);
        roomService.update(getRoom());
        for (Player player : getRoom().getPlayers()) {
            switch (winner) {
                case "村人":
                    if ("村人".equals(player.getIdentity().getName())
                            || "占卜师".equals(player.getIdentity().getName())
                            || "灵能者".equals(player.getIdentity().getName())
                            || "猎人".equals(player.getIdentity().getName())
                            || "共有者".equals(player.getIdentity().getName())
                            || "猫又".equals(player.getIdentity().getName())) {
                        player.setResult("胜利");
                    } else {
                        player.setResult("失败");
                    }
                    break;
                case "人狼":
                    if ("人狼".equals(player.getIdentity().getName())
                            || "狂人".equals(player.getIdentity().getName())
                            || "狂信者".equals(player.getIdentity().getName())
                            || "听狂人".equals(player.getIdentity().getName())) {
                        player.setResult("胜利");
                    } else {
                        player.setResult("失败");
                    }
                    break;
                case "妖狐":
                    if ("妖狐".equals(player.getIdentity().getName())
                            || "背德者".equals(player.getIdentity().getName())) {
                        player.setResult("胜利");
                    } else {
                        player.setResult("失败");
                    }
                    break;
                case "平局":
                    player.setResult("平局");
                    break;
            }
            if (player.getIdentity().getName() == "GM") {
                player.setResult("游戏管理员");
            }
            playerService.update(player);

        }


    }

//    /**
//     * 检查所有角色的夜晚行动是否完成。
//     * @param gameInfo 游戏信息
//     * @return 是否所有行动完成
//     */
//    private boolean checkAllRolesActionsCompleted(GameInfo gameInfo) {
//        return gameInfo.getNightActions().stream().allMatch(GameAction::isCompleted);
//    }

    /**
     * 检验输赢
     *
     * @param
     * @param
     * @return
     */
    public String isGameOver() {
        List<Player> players = this.room.getPlayers();
        String isOver = ConstConfig.GAME_STATUS_NOT_END;
        int murabito_num = 0;
        int werewolf_num = 0;
        int kitsune_num = 0;
        for (Player player : players) {
            if (!player.getIsAlive()) {
                continue;
            }
            String name = player.getIdentity().getName();
            if (name == "人狼") {
                werewolf_num++;
            } else if (name == "村人"
                    || name == "占卜师"
                    || name == "灵能者"
                    || name == "共有者"
                    || name == "猎人"
                    || name == "猫又"
                    || name == "狂人"
                    || name == "狂信者"
                    || name == "背德者"
                    || name == "听狂人") {
                murabito_num++;
            } else if (name == "妖狐") {
                kitsune_num++;
            }
        }

        //判断胜负
        if (werewolf_num == 0) {  //狼死完的情况
            isOver = ConstConfig.GAME_STATUS_OVER_MURABITO_WIN;
            if (kitsune_num > 0) {    //狐仍存活
                isOver = ConstConfig.GAME_STATUS_OVER_KITSUNE_WIN;
            }
        } else if (murabito_num <= werewolf_num) {
            isOver = ConstConfig.GAME_STATUS_OVER_WEREWOLF_WIN;
            if (kitsune_num > 0) {    //狐仍存活
                isOver = ConstConfig.GAME_STATUS_OVER_KITSUNE_WIN;
            }
        }

        return isOver;
    }

    /**
     * 得到房间列表还活着的人
     */

    public List<Player> getAlivePlayers(List<Player> players) {
        List<Player> alivePlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getIsAlive()) {
                alivePlayers.add(player);
            }
        }
        return alivePlayers;
    }

    /**
     * 猫又连坐 对于猫又：控制层调用处刑后检测胜利 未结束即调用此方法连坐
     *
     * @return
     */
    public void votedNekomata() {
        List<Player> alivePlayers = getAlivePlayers(this.room.getPlayers());
        Random random = new Random();

        int index = random.nextInt(alivePlayers.size());
        int roomPlayerId = alivePlayers.get(index).getRoomPlayerId();
        Player nekoedPlayer = getPlayerByRoomPlayerId(room.getPlayers(), roomPlayerId);
        if (nekoedPlayer != null) {
            execute(roomPlayerId);
            GameActionBody nekoVoteActionBody = creatStartedGameActionBody(
                    ConstConfig.GAME_NIGHT_DIE,
                    null,
                    null,
                    Message.system(this.room.getRoomId(), nekoedPlayer.getName() + " 不成样子的尸体被发现了。", redisService, true)
            );
            saveAndNotifyPlayers(nekoVoteActionBody, "/ALL");
            if (nekoedPlayer.getIdentity().getName() == "猫又") {
                votedNekomata();
            }
        }
    }

    /**
     * 处刑
     *
     * @return
     */
    public boolean execute(int roomPlayerId) {
        List<Player> alivePlayers = getAlivePlayers(this.room.getPlayers());

        for (Player player : alivePlayers) {
            if (player.getRoomPlayerId() == roomPlayerId) {
                player.setIsAlive(false);
                return true;
            }
        }
        return false;
    }

    public void checkKitsuneDie() {
        if (this.countIdentity("妖狐").size() == 0 && this.countIdentity("背德者").size() != 0) {
            //妖狐死完了，背德追随
            this.getRoom().getPlayers().stream().filter(player -> player.getIdentity().getName().equals("背德者")).forEach(player -> {
                player.setIsAlive(false);
                String playerName = player.getName();  // 获取玩家名字

                // 为每个死亡玩家构造 GameActionBody
                GameActionBody nightDieActionBody = creatStartedGameActionBody(
                        ConstConfig.GAME_NIGHT_DIE,
                        null,
                        null,
                        Message.system(this.room.getRoomId(), playerName + " 追随者某人死去了。", redisService, true)
                );
                saveAndNotifyPlayers(nightDieActionBody, "/ALL");
            });
        }
    }

    public Player getPlayerByRoomPlayerId(List<Player> players, int roomPlayerId) {
        Player gotPlayer = null;
        for (Player player : players) {
            if (player.getRoomPlayerId() == roomPlayerId) {
                gotPlayer = player;
            }
        }
        return gotPlayer;
    }

    /**
     * 对当前阶段进行加减时操作
     *
     * @param additionalTimeInSeconds
     */
    public void modifyRemainingTime(long additionalTimeInSeconds) {
        phaseTimer.adjustTime(additionalTimeInSeconds);
    }

    /**
     * 通知客户端游戏开始
     */
    public void notifyClientStart() {
        // 通知客户端游戏开始
        Message message = Message.system(this.getRoom().getRoomId(), "游戏开始。", redisService, true);
        //初始化，作通知用的actionbody，不包含每个人的职业信息等
        GameActionBody gameInitgameActionBody = new GameActionBody(ConstConfig.ROOM_GAME_START, message, this.getRoom().PlayersWithIdentityNull());
        //这里仅做推送系统消息用的开始标识，所以设置started为false，避免触发客户端的正式开始函数内容
        gameInitgameActionBody.setGameStarted(false);
        saveAndNotifyPlayers(gameInitgameActionBody, "/ALL");
        //发送给每个人身份信息
        for (Player player : this.getRoom().getPlayers()) {
            String identityName = player.getIdentity().getName();
            GameActionBody identityGameActionBody = new GameActionBody(ConstConfig.GAME_SELF_IDENTITY);
            identityGameActionBody.setGameStarted(true);
            identityGameActionBody.setIdentityName(identityName);
            identityGameActionBody.setDayNum(this.getGameInfo().getDayCount());
            identityGameActionBody.setDayOrNightOrVote(this.getGameInfo().getGameState());
            if (player.getIdentity().getName() == "人狼")
                identityGameActionBody.setPartners(this.countIdentity("人狼").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList())); // 收集为 List<Integer>);
            if (player.getIdentity().getName() == "狂信者")
                identityGameActionBody.setPartners(this.countIdentity("人狼").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList())); // 收集为 List<Integer>);
            if (player.getIdentity().getName() == "共有者")
                identityGameActionBody.setPartners(this.countIdentity("共有者").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList())); // 收集为 List<Integer>);
            if (player.getIdentity().getName() == "妖狐")
                identityGameActionBody.setPartners(this.countIdentity("妖狐").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList())); // 收集为 List<Integer>);
            if (player.getIdentity().getName() == "背德者")
                identityGameActionBody.setPartners(this.countIdentity("妖狐").stream()
                        .map(Player::getRoomPlayerId) // 提取 roomPlayerId
                        .collect(Collectors.toList())); // 收集为 List<Integer>);
            if (identityName == "GM") {
                identityGameActionBody.setRoom(this.getRoom());
            } else {
                identityGameActionBody.setRoom(this.getRoom().PlayersWithIdentityNull());
            }
            saveAndNotifyOnePlayer(identityGameActionBody, player.getUserId());
        }
//        发送给观战者开始信息
        GameActionBody toWatcherGameActionBody = new GameActionBody(ConstConfig.GAME_SELF_IDENTITY, this.getRoom().PlayersWithIdentityNull());

        toWatcherGameActionBody.setGameStarted(true);
        toWatcherGameActionBody.setIdentityName("观战者");
        toWatcherGameActionBody.setDayNum(this.getGameInfo().getDayCount());
        toWatcherGameActionBody.setDayOrNightOrVote(this.getGameInfo().getGameState());
//        toWatcherGameActionBody.setSeconds(this.getPhaseTimer().getRemainingTime() / 1000);
        toWatcherGameActionBody.setRoom(this.getRoom().PlayersWithIdentityNull());
        List<Integer> excludeUserIds = new ArrayList<>();// gm和玩家id列表，除此之外的皆为观战者
        this.getRoom().getPlayers().forEach(player -> excludeUserIds.add(player.getUserId()));
        simpUserRegistry.getUsers().forEach(user -> {
            // 遍历所有连接的用户
            user.getSessions().forEach(session -> {
                // 检查用户是否订阅了目标主题
                if (session.getSubscriptions().stream().anyMatch(sub -> sub.getDestination().equals("/app/room/" + room.getRoomId()))) {
                    // 如果用户不在排除列表中，发送消息
                    if (!excludeUserIds.contains(Integer.valueOf(user.getName()))) {
//                        messagingTemplate.convertAndSendToUser(user.getName(), "/app/room/"+ room.getRoomId(), message);
                        saveAndNotifyOnePlayer(toWatcherGameActionBody, Integer.valueOf(user.getName()));
                    }
                }
            });
        });
    }

    public List<Player> countIdentity(String identityName) {
        return this.getRoom().getPlayers().stream()
                .filter(player -> player.getIdentity().getName().equals(identityName) && player.getIsAlive())
                .collect(Collectors.toList());
    }

    public void nightKill() {
        // 处理 killMap 中的玩家 为狼咬 保留写法为扩展性做准备
        this.gameInfo.getKillMap().forEach((key, value) -> {
            // 查找与当前 killMap 中 value 匹配的玩家并设置死亡
            this.getRoom().getPlayers().stream()
                    .filter(player -> player.getRoomPlayerId().equals(value))
                    .findFirst()
                    .ifPresent(player -> {
                        // 如果该玩家不在 kariudoMap 中
                        if (!this.gameInfo.getKariudoMap().containsValue(value) || !"妖狐".equals(player.getIdentity().getName())) {
                            this.gameInfo.getNightDeadPlayer().put(player.getRoomPlayerId(), "kami");
                            //猫又连坐逻辑
                            if (player.getIdentity().getName().equals("猫又")) {
                                //被连坐者
                                this.gameInfo.getNightDeadPlayer().put(getPlayerByRoomPlayerId(room.getPlayers(),key).getRoomPlayerId(), "neko");
                            }
                        }
                    });
        });
        this.gameInfo.getUranaiMap().forEach((key, value) -> {
            this.getRoom().getPlayers().stream().filter(player -> player.getRoomPlayerId().equals(value)).findFirst().ifPresent(player -> {
                if ("妖狐".equals(player.getIdentity().getName())) {
                    this.gameInfo.getNightDeadPlayer().put(player.getRoomPlayerId(), "uranai");
                }
            });
        });
        long nightDeadKitsuneCount = this.gameInfo.getNightDeadPlayer().values().stream()
                .filter(reason -> "uranai".equals(reason)) // 过滤被占卜师查杀的
                .count();
        if (this.countIdentity("妖狐").size() - nightDeadKitsuneCount <= 0 && this.countIdentity("背德者").size() != 0) {
            //妖狐死完了，背德追随
            this.countIdentity("背德者").forEach(player -> {
                if (!this.gameInfo.getNightDeadPlayer().containsKey(player.getRoomPlayerId())) {
                    this.gameInfo.getNightDeadPlayer().put(player.getRoomPlayerId(), "haitoku");
                }
            });
        }

        //通知死亡
        if (this.gameInfo.getNightDeadPlayer() != null && !this.gameInfo.getNightDeadPlayer().isEmpty()) {
            // 1. 转换为 List 并打乱顺序
            List<Map.Entry<Integer, String>> entryList = new ArrayList<>(this.gameInfo.getNightDeadPlayer().entrySet());
            Collections.shuffle(entryList);

            // 2. 重新放入新的 HashMap 以保持打乱后的顺序
            Map<Integer, String> shuffledMap = new LinkedHashMap<>();
            for (Map.Entry<Integer, String> entry : entryList) {
                shuffledMap.put(entry.getKey(), entry.getValue());
            }
            // 3. 现在 shuffledMap 是乱序的，可以遍历
            shuffledMap.forEach((key, value) -> {
                Player player = getPlayerByRoomPlayerId(room.getPlayers(), key);
                String playerName = player.getName();
                player.setIsAlive(false);
                // 为每个死亡玩家构造 GameActionBody
                GameActionBody nightDieActionBody = null;
                switch (value) {
                    case "neko":
                    case "kami":
                    case "uranai":
                        nightDieActionBody = creatStartedGameActionBody(
                                ConstConfig.GAME_NIGHT_DIE,
                                null,
                                null,
                                Message.system(this.room.getRoomId(), playerName + " 不成样子的尸体被发现了。", redisService, true)
                        );
                        break;
                    case "haitoku":
                        nightDieActionBody = creatStartedGameActionBody(
                                ConstConfig.GAME_NIGHT_DIE,
                                null,
                                null,
                                Message.system(this.room.getRoomId(), playerName + " 追随着某人死去了。", redisService, true)
                        );
                        break;
                }
                saveAndNotifyPlayers(nightDieActionBody, "/ALL");
            });
        } else {
            GameActionBody nightDieActionBody = creatStartedGameActionBody(
                    ConstConfig.GAME_NIGHT_DIE,
                    null,
                    null,
                    Message.system(this.room.getRoomId(), "迎来了和平的早晨。", redisService, false)
            );
            saveAndNotifyPlayers(nightDieActionBody, "/ALL");
        }
        this.gameInfo.getKillMap().clear();
        this.gameInfo.getKariudoMap().clear();
        if (this.gameInfo.getNightDeadPlayer() != null) {
            this.gameInfo.getNightDeadPlayer().clear();
        }
    }

    public String rebuildVoteInfoJsonString() {
        // 用于统计每个被投票人被投票的次数
        Map<Integer, Integer> voterCountMap = new HashMap<>();
        String resultString = "         投票结果            \n";

        // 第一遍遍历，统计每个被投票人被投票的次数
        for (Map.Entry<Integer, Integer> entry : this.gameInfo.getVoteInfo().entrySet()) {
            // 增加被投票人的投票次数
            voterCountMap.put(entry.getValue(), voterCountMap.getOrDefault(entry.getValue(), 0) + 1);
        }

        // 第二遍遍历，构建投票信息字符串
        for (Map.Entry<Integer, Integer> entry : this.gameInfo.getVoteInfo().entrySet()) {
            String voterName = this.room.getPlayerByRoomPlayerId(entry.getKey()).getName(); // 投票人名字
            String votedName = this.room.getPlayerByRoomPlayerId(entry.getValue()).getName(); // 被投票人名字

            // 获取被投票人的总票数
            int votedPlayerTotalVotes = voterCountMap.getOrDefault(entry.getKey(), 0);
            // 构建投票信息字符串
            resultString = resultString + "[ " + votedPlayerTotalVotes + " 票]       " + voterName + "        =>      " + votedName + "\n";
        }

        return resultString;
    }

    public void endInitial() {
        room.getPlayers().forEach(player -> {
            //准备gameActionBody，用于返回给前端，初始化游戏状态
            GameActionBody gameActionBody = new GameActionBody(ConstConfig.ROOM_INITIAL, room.getRoomId());
            gameActionBody.setRoom(room);
            List<Message> msgs = null;
            msgs = messageService.loadMessagesByRoomId(room.getRoomId());
            gameActionBody.setGameStarted(false);
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(player.getUserId()),              // 用户标识
                    "/queue/" + room.getRoomId() + "/messages",       // 匹配的队列路径
                    msgs                 // 要发送的消息内容
            );
            /**
             * 通信当前游戏状态
             */
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(player.getUserId()),              // 用户标识
                    "/queue/" + room.getRoomId() + "/messages",       // 匹配的队列路径
                    gameActionBody);               // 要发送的消息内容
        });

    }

    public void GmKill(Integer roomPlayerId){
        this.getRoom().getPlayers().stream().filter(player -> player.getRoomPlayerId().equals(roomPlayerId)).findFirst().ifPresent(player -> {
            player.setIsAlive(false);
        });
    }

    public void GmRevive(Integer roomPlayerId){
        this.getRoom().getPlayers().stream().filter(player -> player.getRoomPlayerId().equals(roomPlayerId)).findFirst().ifPresent(player -> {
            player.setIsAlive(true);
        });
    }
}
