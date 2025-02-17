package com.jinrou.jinrouwerewolf.util;

import com.jinrou.jinrouwerewolf.entity.Identity.*;
import com.jinrou.jinrouwerewolf.entity.Identity.Identity;

/**
 * @Author: nanaya
 * @Date: 2024/07/19/20:28
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description: 静态版本的角色工厂
 */
public class GameUtil {

    // 将方法改为静态
    public static Identity createIdentity(String name) {
        switch (name) {
            case "GM":
                return new GM();
            case "占卜师":
                return new Uranaishi();
            case "村人":
                return new Murabito();
            case "猎人":
                return new Kariudo();
            case "灵能者":
                return new Reibai();
            case "共有者":
                return new Kouyuu();
            case "猫又":
                return new Nekomata();
            case "人狼":
                return new Werewolf();
            case "狂人":
                return new Kyoujin();
            case "妖狐":
                return new Kitsune();
            case "背德者":
                return new Haitokushya();
            case "none":
            default:
                return null; // 未知角色返回 null
        }
    }
}
