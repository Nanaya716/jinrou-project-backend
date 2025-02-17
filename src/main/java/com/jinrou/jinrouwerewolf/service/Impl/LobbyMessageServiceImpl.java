package com.jinrou.jinrouwerewolf.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinrou.jinrouwerewolf.dao.GameSettingsDao;
import com.jinrou.jinrouwerewolf.entity.Game.GameSettings;
import com.jinrou.jinrouwerewolf.entity.LobbyMessage;
import com.jinrou.jinrouwerewolf.dao.LobbyMessageDao;
import com.jinrou.jinrouwerewolf.service.GameSettingsService;
import com.jinrou.jinrouwerewolf.service.LobbyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


/**
 * (LobbyMessage)表服务实现类
 *
 * @author makejava
 * @since 2025-02-18 01:13:45
 */
@Service("lobbyMessageService")
public class LobbyMessageServiceImpl extends ServiceImpl<LobbyMessageDao, LobbyMessage> implements LobbyMessageService {
    @Autowired
    private LobbyMessageDao lobbyMessageDao;

    /**
     * 通过ID查询单条数据
     *
     * @param lobbyMessageId 主键
     * @return 实例对象
     */
    @Override
    public LobbyMessage queryById(Integer lobbyMessageId) {
        return this.lobbyMessageDao.queryById(lobbyMessageId);
    }

    /**
     * 分页查询
     *
     * @param lobbyMessage 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<LobbyMessage> queryByPage(LobbyMessage lobbyMessage, PageRequest pageRequest) {
        long total = this.lobbyMessageDao.count(lobbyMessage);
        return new PageImpl<>(this.lobbyMessageDao.queryAllByLimit(lobbyMessage, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param lobbyMessage 实例对象
     * @return 实例对象
     */
    @Override
    public LobbyMessage insert(LobbyMessage lobbyMessage) {
        this.lobbyMessageDao.insert(lobbyMessage);
        return lobbyMessage;
    }

    /**
     * 修改数据
     *
     * @param lobbyMessage 实例对象
     * @return 实例对象
     */
    @Override
    public LobbyMessage update(LobbyMessage lobbyMessage) {
        this.lobbyMessageDao.update(lobbyMessage);
        return this.queryById(lobbyMessage.getLobbyMessageId());
    }

    /**
     * 通过主键删除数据
     *
     * @param lobbyMessageId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer lobbyMessageId) {
        return this.lobbyMessageDao.deleteById(lobbyMessageId) > 0;
    }
}
