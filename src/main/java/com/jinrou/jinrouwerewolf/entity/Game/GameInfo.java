package com.jinrou.jinrouwerewolf.entity.Game;

import lombok.*;

import java.util.*;

/**
 * @Author: nanaya
 * @Date: 2024/07/19/21:30
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 * 游戏信息类
 * 用于处理白天夜晚是否有人已经操作，得到如投票等信息，用来判定是否进白天夜晚等
 * 以及controller中控制游戏进行
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GameInfo {
    //默认每人票数 1票 后续可能维护
    public static final double DEFAULT_MARK = 1;

    private Integer roomId;
    private boolean gmInRoom; //是否GM在房间
    private int dayCount;  // 第x天
    /**
     * DAY,
     * NIGHT,
     * MORNING,
     * VOTING,
     * SUSPEND,
     * ENDED
     */
    //
    private String gameState; //白天 黑夜 黎明 投票 结束
    private GameSettings gameSettings; //通过房间的setting属性保存的gamesettings;
//    private volatile long remainingTime; // 当前阶段剩余时间（毫秒）
    /**
     * 某天狼是否已经确定咬对象
     * key：咬人者的roomPlayerId
     * value：狼咬对象的roomPlayerId
     */
    private Map<Integer,Integer> KillMap = new HashMap<>();
    /**
     * 某天占卜是否已经确定占卜对象
     * key：占卜师的的roomPlayerId
     * value：占卜对象的roomPlayerId
     * 结算完清空
     */
    private Map<Integer,Integer> UranaiMap = new HashMap<>();
    /**
     * 某天猎人是否已经确定护卫对象
     * key：猎人的roomPlayerId
     * value：护卫对象的roomPlayerId
     * 结算完清空
     */
    private Map<Integer,Integer> KariudoMap = new HashMap<>();

    /**
     * 每人被投票信息 每次投票完都应该清空 用来结算用，不展示
     * key：投票者roomPlayerId
     * value: 被投票的roomPlayerId
     */
    private Map<Integer, Integer> voteInfo = new HashMap<>();
    /**
     * 夜晚死亡玩家通知列表
     * key ：死亡玩家的roomPlayerId
     * value：死亡玩家的死法
     */
    private Map<Integer, String> nightDeadPlayer = new HashMap<>();

    /**
     * 计算每个玩家被投票的次数，包装成Map返回
     * key:被投票者playerId
     * value：该玩家被投票数
     * @return Map<Integer, Double>
     */
    public Map<Integer, Double> calculateVoteCounts() {
        Map<Integer, Double> voteCounts = new HashMap<>();

        // 遍历voteInfo，计算每个玩家被投票的次数
        for (Integer votedRoomPlayerId : this.voteInfo.values()) {
            voteCounts.put(votedRoomPlayerId, voteCounts.getOrDefault(votedRoomPlayerId, 0.0) + 1);
        }

        return voteCounts;
    }

    /**
     * 计算投票结果是否平票
     * @return 如果存在平票返回 -1，否则返回得票最多的roomPlayerid
     */
    public int getVoteResult() {

        //调用计算方法得到每个人的得票数，得到key playerId value 被投票数的map
        Map<Integer,Double> voteMap = calculateVoteCounts();
        if (voteMap == null || voteMap.isEmpty()) {
            return -1;
        }

        // 将Map转换为List以方便排序
        List<Map.Entry<Integer, Double>> sortedVotes = new ArrayList<>(voteMap.entrySet());
        sortedVotes.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        // 获取最高票数和第二高票数
        double highestVote = sortedVotes.get(0).getValue();
        double secondHighestVote = sortedVotes.size() > 1 ? sortedVotes.get(1).getValue() : Double.NEGATIVE_INFINITY;

        // 检查是否有平票
        if (highestVote == secondHighestVote) {
            return -1; // 平票
        }

        // 返回得票最多的选项
        return sortedVotes.get(0).getKey();
    }

    public void incrementDayCount(){
        this.dayCount++;
    }
}

