package com.jinrou.jinrouwerewolf.entity;

import lombok.*;

/**
 * @Author: nanaya
 * @Date: 2025/01/26/15:00
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class VoteDetailObject {
    private String voterName;
    private Integer beVotedCount;
    private String votedName;
}
