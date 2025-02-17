package com.jinrou.jinrouwerewolf.service.Impl;

/**
 * @Author: nanaya
 * @Date: 2024/07/20/7:00
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:主要是开始游戏前的一些操作
 */

import com.jinrou.jinrouwerewolf.entity.Game.*;
import com.jinrou.jinrouwerewolf.entity.Identity.*;
import com.jinrou.jinrouwerewolf.service.PlayerService;
import com.jinrou.jinrouwerewolf.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class GameService {

    private GlobalData globalData = GlobalData.getInstance();
    @Autowired
    private RoomService roomService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PlayerService playerService;

    /**
     * 根据房间号和房主检查并获取房间实体
     *
     * @param roomId 房间号
     * @param owner  房主id
     * @return room
     */
    public Room checkAndGetRoom(int roomId, int owner) {

//        Room room = globalData.getRoomData().get(roomId);
        Room room = roomService.getRoomById(roomId);
        if (room == null || room.getPlayers().get(0).getRoomPlayerId() != owner) {
            return null;
        }
        return room;
    }

    /**
     * 根据房间号检查并获取房间
     *
     * @param roomId 房间号
     * @return room
     */
    public Room checkAndGetRoom(int roomId) {

        Room room = globalData.getRoomData().get(roomId);
        if (room == null) {
            return null;
        }
        return room;
    }

    /**
     * 通过房间号获取游戏详细信息
     *
     * @param roomId
     * @return
     */
    public GameInfo getGameInfo(int roomId) {
        return globalData.getGameData().get(roomId);
    }

    /**
     * 准备/离开游戏
     *
     * @param userId 玩家id
     * @param roomId 房间id
     * @return 准备状态 -1为房间不存在 0未准备 1准备
     */
    public int readyGame(int userId, int roomId) {
        Room room = globalData.getRoomData().get(roomId);
        if (room == null) {
            return -1;
        }
        int readyState = 0; //准备状态
        Player player = room.getPlayers().get(userId);
        if (player != null) {
            if (player.getIsReady()) {
                player.setIsReady(false);
                readyState = 0;
            } else {
                player.setIsReady(true);
                readyState = 1;
            }
        }
        return readyState;
    }

    /**
     * 确认房间内是否全都准备
     *
     * @param room
     * @return
     */
    public boolean isAllReady(Room room) {
        List<Player> players = room.getPlayers();
        int owner = room.getRoomCreatorId();
        boolean isReady = true;
        for (Player player : players) {
            if (!player.getIsReady() && player.getUserId() != owner) {
                isReady = false;
                break;
            }
        }
        return isReady;
    }

    /**
     * （开始游戏时）确认游戏设置是否合法
     *
     * @param room
     * @return
     */
    public boolean isSettingValidate(Room room) {
        GameSettings settings = room.getGameSettings();

        if(settings.isGmMode()){
            if (settings.getIdentityList().size() > room.getPlayers().size()
                    || !settings.getIdentityList().contains("人狼")) {
                System.out.println("职业设置不合法");
                return false;
            }
        }else{
            if (settings.getIdentityList().size() - 1 > room.getPlayers().size()
                    || !settings.getIdentityList().contains("人狼")) {
                System.out.println("职业设置不合法");
                return false;
            }
        }


        return true;
    }

    /**
     * 通过用户id查询游戏内玩家号 0为未查找到
     *
     * @param userId
     * @return
     */
    public int getPlayerIdByUserId(int userId, int roomId) {
        Room room = roomService.getRoomById(roomId);
        List<Player> playerList = room.getPlayers();

        for (Player player : playerList) {
            if (player.getUserId() == userId) {
                return player.getRoomPlayerId();
            }
        }
        return 0;
    }

    /**
     * 开始游戏 失败返回null
     */
    public GameInfo initGame(Room room) {
        if (!isSettingValidate(room)) {
            return null;
        }
        ;
        // 替身君加入
        if (!room.getPlayers().stream()
                .anyMatch(player -> player.getRoomPlayerId().equals(0))) {
            Player playerTishen = Player.createFirstVictim(room.getRoomId());
            playerService.save(playerTishen);
            room.getPlayers().add(playerTishen);
        }


        GameInfo gameInfo = new GameInfo();
        gameInfo.setRoomId(room.getRoomId());
        gameInfo.setGameSettings(room.getGameSettings());
        if (room.getPlayerByUserId(room.getRoomCreatorId()) == null) {
            //房主不在房间内
            gameInfo.setGmInRoom(false);
        } else {
            gameInfo.setGmInRoom(true);
        }
        gameInfo.setGameState("NIGHT");
        gameInfo.setDayCount(1);
        List<Player> allotedPlayers = allotRoles(room); // 分配角色
        if(allotedPlayers == null){
            return null;
        }else{
            allotedPlayers.forEach(player -> {
                player.setIsAlive(true);
                playerService.update(player);
            });

        }
        for (Player player : room.getPlayers()) {
            if (player.getIdentity().getName().equals("GM")) {
                gameInfo.setGmInRoom(true);
                break;
            }
        }
        return gameInfo;
    }

    /**
     * 分配角色
     *
     * @return 角色列表
     */
    public List<Player> allotRoles(Room room) {
        // 获取房间中的玩家和游戏设置
        List<Player> players = room.getPlayers();
        GameSettings gameSettings = room.getGameSettings();

        // 是否启用希望模式（默认为否）
        boolean hopeMode = false; // gameSettings.getIsHopeMode();
        // 是否启用gm制 默认是
        boolean gmMode = gameSettings.isGmMode();
        boolean firstVictimSafe = gameSettings.getIsFirstVictims();

        // 获取职业列表
        List<String> identityList = new ArrayList<>(gameSettings.getIdentityList()); // 避免直接修改原始列表
        Random random = new Random();
        //房间内有房主时找到房主，否则为null
        Player creatorPlayer = room.getPlayers().stream()
                .filter(player -> player.getUserId().equals(room.getRoomCreatorId()))
                .findFirst()
                .orElse(null);
        if (creatorPlayer != null && gmMode) {        // 房主在房间内，并且为GM制
            creatorPlayer.setIdentity(getIdentityByName("GM")); // 设置GM的身份
            // 如果职业数量不足，则补充为村人
            while (identityList.size() < players.size() - 1) { // -1是GM除外
                identityList.add("村人");
            }

            if(firstVictimSafe){
                if(!identityList.contains("村人")){
                    return null;
                }
            }

            List<Player> firstVictimDiePlayers = firstVictimRole(players, identityList, firstVictimSafe);

            // 随机分配职业给玩家
            for (Player player : firstVictimDiePlayers) {
                if (!player.getUserId().equals(creatorPlayer.getUserId()) && player.getRoomPlayerId() != 0) {
                    int randomIndex = random.nextInt(identityList.size()); // 随机选择一个职业
                    String identityName = identityList.get(randomIndex); // 获取职业名称
                    player.setIdentity(getIdentityByName(identityName)); // 设置玩家的职业
                    identityList.remove(randomIndex); // 从职业列表中移除已分配的职业
                }
            }
        } else {
            while (identityList.size() < players.size()) {
                identityList.add("村人"); // 添加默认身份村人
            }

            if(firstVictimSafe){
                if(!identityList.contains("村人")){
                    return null;
                }
            }
            List<Player> firstVictimDiePlayers = firstVictimRole(players, identityList, firstVictimSafe);
            if(firstVictimDiePlayers == null){
                return null;
            }
            // 随机分配职业给玩家
            for (Player player : firstVictimDiePlayers) {
                if (player.getRoomPlayerId() != 0) {
                    int randomIndex = random.nextInt(identityList.size()); // 随机选择一个职业
                    String identityName = identityList.get(randomIndex); // 获取职业名称
                    player.setIdentity(getIdentityByName(identityName)); // 设置玩家的职业
                    identityList.remove(randomIndex); // 从职业列表中移除已分配的职业
                }
            }
        }

        // 返回分配了职业的玩家列表
        return players;
    }

    //分配初日牺牲者的身份
    private List<Player> firstVictimRole(List<Player> players, List<String> identityList, boolean FirstVictimSafe) {
        Random random = new Random();
        Player firstVictim = players.stream().filter(player -> player.getRoomPlayerId().equals(0)).findFirst().orElse(null);
        if (FirstVictimSafe) {
            firstVictim.setIdentity(getIdentityByName("村人"));
            identityList.remove("村人"); // 从职业列表中移除已分配的职业
        } else {
            String identityName = null;
            // 检查 identityList 是否包含至少一个不是 "猫又"、"人狼"、"妖狐" 的职业
            if(identityList.stream()
                    .anyMatch(identity -> !("猫又".equals(identity) || "人狼".equals(identity) || "妖狐".equals(identity)))){
                do{
                    int randomIndex = random.nextInt(identityList.size()); // 随机选择一个职业
                    identityName = identityList.get(randomIndex); // 获取职业名称
                } while ("猫又".equals(identityName) || "人狼".equals(identityName) || "妖狐".equals(identityName));
                firstVictim.setIdentity(getIdentityByName(identityName)); // 设置玩家的职业
                identityList.remove(identityName); // 从职业列表中移除已分配的职业
            }else{
                return null;
            }

        }

        return players;
    }

    /**
     * 通过职业name获取职业对象
     *
     * @param name
     * @return Identity
     */
    public Identity getIdentityByName(String name) {
        Identity identity = null;
        switch (name) {
            case "村人":
                identity = new Murabito();
                break;
            case "占卜师":
                identity = new Uranaishi();
                break;
            case "猎人":
                identity = new Kariudo();
                break;
            case "灵能者":
                identity = new Reibai();
                break;
            case "共有者":
                identity = new Kouyuu();
                break;
            case "猫又":
                identity = new Nekomata();
                break;
            case "人狼":
                identity = new Werewolf();
                break;
            case "妖狐":
                identity = new Kitsune();
                break;
            case "背德者":
                identity = new Haitokushya();
                break;
            case "狂人":
                identity = new Kyoujin();
                break;
            case "狂信者":
                identity = new Kyoushinjya();
                break;
            case "GM":
                identity = new GM();
                break;
            case "none":
                break;
        }
        return identity;
    }
}

