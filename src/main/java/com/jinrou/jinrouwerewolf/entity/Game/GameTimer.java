package com.jinrou.jinrouwerewolf.entity.Game;

import lombok.*;

/**
 * @Author: nanaya
 * @Date: 2024/08/26/15:24
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameTimer implements Runnable {
    private int roomId;
    private volatile long remainingTime; // 剩余时间（毫秒）(不管白天夜晚投票，剩余时间都是这个）
    private int type; //0-白天讨论，1-夜晚，2-投票 3-犹豫 4-黎明

    public GameTimer(int roomId) {
        this.roomId = roomId;
    }

    //TODO: 计数时间
    @Override
    public void run() {
        while (remainingTime > 0) {
            try {
                Thread.sleep(1000); // 每隔指定时间更新一次
                remainingTime -= 1;
                System.out.println(this.getRoomId()+ "房间剩余时间：" + remainingTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                break;
            }
        }
        remainingTime = 0; // 时间到，设置为0
    }
}

