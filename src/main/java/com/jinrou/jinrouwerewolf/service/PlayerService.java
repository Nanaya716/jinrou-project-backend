package com.jinrou.jinrouwerewolf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinrou.jinrouwerewolf.entity.Game.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (Player)表服务接口
 *
 * @author makejava
 * @since 2025-01-20 06:25:44
 */
public interface PlayerService extends IService<Player> {

    /**
     * 通过ID查询单条数据
     *
     * @param playerId 主键
     * @return 实例对象
     */
    Player queryById(Integer playerId);

    /**
     * 分页查询
     *
     * @param player 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<Player> queryByPage(Player player, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param player 实例对象
     * @return 实例对象
     */
    Player insert(Player player);

    /**
     * 修改数据
     *
     * @param player 实例对象
     * @return 实例对象
     */
    Player update(Player player);

    /**
     * 通过主键删除数据
     *
     * @param playerId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer playerId);
    boolean removeByUserIdAndRoomId(Integer userId, Integer roomId);
    Player QueryByUserIdAndRoomId(Integer userId, Integer roomId);
    Player QueryByRoomPlayerIdAndRoomId(Integer userId, Integer roomId);
    int updateByUserIdAndRoomId(Player player);
    Player queryByRoomIdAndUserId(Integer roomId,Integer userId);
}
