package com.jinrou.jinrouwerewolf.entity.Identity;

import com.jinrou.jinrouwerewolf.entity.Game.GameInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: nanaya
 * @Date: 2024/07/18/3:37
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nekomata implements Identity {
    private String name = "猫又";

    public int dayKill(GameInfo gameInfo){
        return 0;
    }

    public int nightKill(GameInfo gameInfo){
        return 0;
    }
}
