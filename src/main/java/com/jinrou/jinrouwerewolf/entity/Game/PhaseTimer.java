package com.jinrou.jinrouwerewolf.entity.Game;

import com.jinrou.jinrouwerewolf.controller.GameController;
import com.jinrou.jinrouwerewolf.controller.GameTask;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * PhaseTimer类用于处理游戏阶段的时间控制，支持计时、调整剩余时间、停止计时以及阶段结束后的回调处理。
 * 使用ScheduledExecutorService实现定时任务。
 */
public class PhaseTimer {
    // 创建一个线程池，用于定时执行任务
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // 保存定时任务的句柄，用于控制任务的取消
    private ScheduledFuture<?> scheduledTask;
    // 剩余时间，单位是毫秒
    private volatile long remainingTime;
    private final CountDownLatch latch = new CountDownLatch(1); // 用于通知主线程计时结束
    private Integer roomId;
    private GameController gameController;


    /**
     * 构造函数，初始化计时器并设置初始时间
     *
     * @param initialTimeMillis 初始剩余时间，单位是毫秒
     */
    public PhaseTimer(long initialTimeMillis, Integer roomId, GameController gameController) {
        this.remainingTime = initialTimeMillis;
        this.roomId = roomId;
        this.gameController = gameController;
    }

    /**
     * 启动计时器，开始定时任务。
     * 该方法会每秒钟减少剩余时间，并根据时间或角色的完成状态决定是否停止计时。
     *
     * @param last      是否持续判断需要结束
     * @param onTimeout 超时回调函数，时间到达后会执行
     */
    public void start(Runnable onTimeout, boolean last, GameActionBody gameActionBody) {
        GameTask gametask = gameController.getGameTask(this.roomId);
        String nowState = gametask.getGameInfo().getGameState();
        if (gameActionBody != null) {
            if (gameActionBody.getMessage().getMessageContent() == null) {
                gametask.NotifyPlayers(gameActionBody, "/ALL");
            } else {
                gametask.saveAndNotifyPlayers(gameActionBody, "/ALL");
            }
        }

        // 启动一个定时任务，每秒执行一次 fixrate不管执行时间有多长 固定频率
        scheduledTask = scheduler.scheduleAtFixedRate(() -> {
            synchronized (this) {
                remainingTime -= 1000; // 每秒减少 1 秒
                if (last) {
                    //持续判断是否应该结束计时
                    switch (nowState) {
                        case "VOTING":
                        case "SUSPEND":
                            List<Player> filteredPlayers = gametask.getRoom().getPlayers().stream()
                                    .filter(player -> player.getRoomPlayerId() != 0 && player.getIsAlive()) // 过滤掉 roomPlayerId == 0 的玩家
                                    .toList();
                            //GM制且GM在房间里时，GM除外
                            if (gametask.getRoom().getGameSettings().isGmMode() && gametask.getGameInfo().isGmInRoom()) {
                                if (gametask.getGameInfo().getVoteInfo().size() == filteredPlayers.size() - 1) {
                                    stop(onTimeout); // 停止计时
                                }
                            } else {
                                if (gametask.getGameInfo().getVoteInfo().size() == filteredPlayers.size()) {
                                    stop(onTimeout); // 停止计时
                                }
                            }
                            break;
                        case "MORNING":
                            if (gametask.getGameInfo().getUranaiMap().size() >= gametask.countIdentity("占卜师").size()
                                    && gametask.getGameInfo().getKillMap().size() > 0
                                    && (gametask.getGameInfo().getDayCount() == 1 || gametask.getGameInfo().getKariudoMap().size() >= gametask.countIdentity("猎人").size())) {
                                stop(onTimeout); // 停止计时
                            }
                            break;
                    }
                }

                // 如果剩余时间小于等于 0，超时处理
                if (remainingTime <= 0) {
                    stop(onTimeout); // 停止计时
                }
            }
        }, 1, 1, TimeUnit.SECONDS); // 延迟 0 毫秒开始，每秒执行一次
    }

    /**
     * 调整计时器的剩余时间。
     *
     * @param adjustmentMillis 调整时间的毫秒数，可以为负数表示缩短时间，为正数表示延长时间
     */
    public void adjustTime(long adjustmentMillis) {
        synchronized (this) {
            remainingTime += adjustmentMillis; // 调整剩余时间
        }
    }

    /**
     * 重置计时器的剩余时间。
     *
     * @param resetTimeMillis
     */
    public void resetTime(long resetTimeMillis) {
        synchronized (this) {
            remainingTime = resetTimeMillis;
        }
    }

    /**
     * 停止计时器，取消定时任务
     */
    public void stop(Runnable callback) {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true); // 取消定时任务
        }

        // 执行回调函数（如果有提供）
        if (callback != null) {
            callback.run();
        }
        latch.countDown(); // 通知主线程计时结束
    }

    /**
     * 等待计时器完成
     *
     * @throws InterruptedException
     */
    public void awaitCompletion() throws InterruptedException {
        latch.await(); // 等待计时器结束
    }

    /**
     * 关闭定时器和线程池
     */
    public void shutdown() {
        scheduler.shutdown(); // 关闭线程池
    }

    /**
     * 检查是否所有角色的行动已经完成。
     * 该方法可以根据游戏逻辑进行修改，当前是一个简单的模拟。
     *
     * @return 如果所有角色的行动已完成，则返回 true，否则返回 false
     */
    private boolean checkAllRolesActionsCompleted() {
        // 这里模拟角色的行动完成状态，实际应该调用游戏中其他方法来判断
        return false;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

}
