package com.jinrou.jinrouwerewolf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinrou.jinrouwerewolf.entity.Game.Message;
import com.jinrou.jinrouwerewolf.entity.User;

import java.util.List;

/**
 * @Author: nanaya
 * @Date: 2024/07/08/7:45
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
public interface MessageService extends IService<Message> {
//    User getUserById(int id);
//    User AuthorizeUser(String username, String password);
    List<Message> loadMessagesByRoomIdAndLimit(String roomId, int limit);
    List<Message> loadMessagesByRoomId(Integer roomId);
    List<Message> loadMessagesByRoomIdAndRoomPlayerIdAndChannel(Integer roomId,Integer roomPlayerId, List<String> channels,Integer userId);
}
