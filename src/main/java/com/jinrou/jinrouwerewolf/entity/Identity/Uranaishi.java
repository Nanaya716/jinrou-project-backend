package com.jinrou.jinrouwerewolf.entity.Identity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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
public class Uranaishi implements Identity {
    private String name = "占卜师";

    private boolean isUranai = false;
    public String Uranai(Identity identity){
        if (!isUranai){
            isUranai = true;
        }
        return identity.getName();
    }

    public void resetUranai(){
        this.isUranai = false;
    }
}
