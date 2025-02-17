package com.jinrou.jinrouwerewolf.entity.Game;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jinrou.jinrouwerewolf.TypeHandler.ListToStringHandler;
import com.jinrou.jinrouwerewolf.entity.Identity.Identity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: nanaya
 * @Date: 2024/07/16/21:48
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("game_settings")
public class GameSettings {
    private int gameSettingId;
    //是否有初日牺牲者
    private Boolean isFirstVictims;
    //希望役职制
    private Boolean isHopeMode;
    //白天时长（以秒为单位）
    private int dayDuration;
    //设定夜晚时长
    private int nightDuration;
    //设定投票时长
    private int voteDuration;
    private int morningDuration;
    //n秒规则
    @JsonProperty("nSecondRule")
    private int nSecondRule;
    //是否允许猎人连续护卫
    private Boolean hunterContinuousGuarding;
    //房主设定的职业List 只存职业名字 元素数量即为设定的职业数量
    @TableField(typeHandler = ListToStringHandler.class)
    private List<String> identityList = new ArrayList<>();
    private boolean gmMode;

}
