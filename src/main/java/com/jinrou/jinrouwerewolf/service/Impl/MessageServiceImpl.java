package com.jinrou.jinrouwerewolf.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinrou.jinrouwerewolf.dao.MessageDao;
import com.jinrou.jinrouwerewolf.dao.UserDao;
import com.jinrou.jinrouwerewolf.entity.Game.Message;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.service.MessageService;
import com.jinrou.jinrouwerewolf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: nanaya
 * @Date: 2025/01/07/4:55
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements MessageService {
    @Autowired
    private MessageDao messageDao;

    public List<Message> loadMessagesByRoomIdAndLimit(String roomId, int limit){
//        QueryWrapper<Message> wrapper = new QueryWrapper<>();
//        wrapper.eq("room_id", roomId);
//        messageDao.selectList(wrapper);

        return messageDao.loadMessagesByRoomIdAndLimit(roomId,limit);
    }

    @Override
    public List<Message> loadMessagesByRoomId(Integer roomId) {
        return messageDao.loadMessagesByRoomId(roomId);
    }

    @Override
    public List<Message> loadMessagesByRoomIdAndRoomPlayerIdAndChannel(Integer roomId, Integer roomPlayerId, List<String> channels,Integer userId) {
        return messageDao.loadMessagesByRoomIdAndRoomPlayerIdAndChannel(roomId,roomPlayerId,channels,userId);
    }



}
