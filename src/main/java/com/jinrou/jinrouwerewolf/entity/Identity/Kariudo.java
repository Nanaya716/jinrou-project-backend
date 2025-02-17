package com.jinrou.jinrouwerewolf.entity.Identity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
public class Kariudo implements Identity {
    private List<Integer> guardList = new ArrayList<>();
    private final String name = "猎人";

    public boolean setGuardian(int guard){
        if(guardList.size()==0){
            guardList.add(guard);
        }else{
            //已经守护则不可继续守护
            if(guardList.get(guardList.size()-1)==guard){
                return false;
            }
        }
        return true;
    }

    public void resetGuardian(int guard){
        guardList.clear();
    }
}
