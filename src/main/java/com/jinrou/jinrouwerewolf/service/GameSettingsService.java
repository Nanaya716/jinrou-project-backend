package com.jinrou.jinrouwerewolf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinrou.jinrouwerewolf.entity.Game.GameSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (GameSettings)表服务接口
 *
 * @author makejava
 * @since 2025-01-19 07:37:30
 */
public interface GameSettingsService extends IService<GameSettings> {

    /**
     * 通过ID查询单条数据
     *
     * @param gameSettingId 主键
     * @return 实例对象
     */
    GameSettings queryById(Integer gameSettingId);

    /**
     * 分页查询
     *
     * @param gameSettings 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<GameSettings> queryByPage(GameSettings gameSettings, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param gameSettings 实例对象
     * @return 实例对象
     */
    GameSettings insert(GameSettings gameSettings);

    /**
     * 修改数据
     *
     * @param gameSettings 实例对象
     * @return 实例对象
     */
    GameSettings update(GameSettings gameSettings);

    /**
     * 通过主键删除数据
     *
     * @param gameSettingId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer gameSettingId);

}
