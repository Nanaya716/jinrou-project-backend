package com.jinrou.jinrouwerewolf.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinrou.jinrouwerewolf.entity.Game.GameSettings;
import com.jinrou.jinrouwerewolf.dao.GameSettingsDao;
import com.jinrou.jinrouwerewolf.service.GameSettingsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


/**
 * (GameSettings)表服务实现类
 *
 * @author makejava
 * @since 2025-01-19 07:37:30
 */
@Service("gameSettingsService")
public class GameSettingsServiceImpl extends ServiceImpl<GameSettingsDao, GameSettings> implements GameSettingsService {
    @Autowired
    private GameSettingsDao gameSettingsDao;

    /**
     * 通过ID查询单条数据
     *
     * @param gameSettingId 主键
     * @return 实例对象
     */
    @Override
    public GameSettings queryById(Integer gameSettingId) {
        return this.gameSettingsDao.queryById(gameSettingId);
    }

    /**
     * 分页查询
     *
     * @param gameSettings 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<GameSettings> queryByPage(GameSettings gameSettings, PageRequest pageRequest) {
        long total = this.gameSettingsDao.count(gameSettings);
        return new PageImpl<>(this.gameSettingsDao.queryAllByLimit(gameSettings, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param gameSettings 实例对象
     * @return 实例对象
     */
    @Override
    public GameSettings insert(GameSettings gameSettings) {
        this.gameSettingsDao.insert(gameSettings);
        return gameSettings;
    }

    /**
     * 修改数据
     *
     * @param gameSettings 实例对象
     * @return 实例对象
     */
    @Override
    public GameSettings update(GameSettings gameSettings) {
        this.gameSettingsDao.update(gameSettings);
        return this.queryById(gameSettings.getGameSettingId());
    }

    /**
     * 通过主键删除数据
     *
     * @param gameSettingId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer gameSettingId) {
        return this.gameSettingsDao.deleteById(gameSettingId) > 0;
    }
}
