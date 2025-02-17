package com.jinrou.jinrouwerewolf.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinrou.jinrouwerewolf.dao.MessageDao;
import com.jinrou.jinrouwerewolf.entity.Game.Message;
import com.jinrou.jinrouwerewolf.entity.Game.Player;
import com.jinrou.jinrouwerewolf.dao.PlayerDao;
import com.jinrou.jinrouwerewolf.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * (Player)表服务实现类
 *
 * @author makejava
 * @since 2025-01-20 06:25:44
 */
@Service("playerService")
public class PlayerServiceImpl extends ServiceImpl<PlayerDao, Player> implements PlayerService {
    @Autowired
    private PlayerDao playerDao;

    /**
     * 通过ID查询单条数据
     *
     * @param playerId 主键
     * @return 实例对象
     */
    @Override
    public Player queryById(Integer playerId) {
        return this.playerDao.queryById(playerId);
    }

    /**
     * 分页查询
     *
     * @param player 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Player> queryByPage(Player player, PageRequest pageRequest) {
        long total = this.playerDao.count(player);
        return new PageImpl<>(this.playerDao.queryAllByLimit(player, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param player 实例对象
     * @return 实例对象
     */
    @Override
    public Player insert(Player player) {
        this.playerDao.insert(player);
        return player;
    }

    /**
     * 修改数据
     *
     * @param player 实例对象
     * @return 实例对象
     */
    @Override
    public Player update(Player player) {
        this.playerDao.update(player);
        return this.queryById(player.getPlayerId());
    }

    /**
     * 通过主键删除数据
     *
     * @param playerId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer playerId) {
        return this.playerDao.deleteById(playerId) > 0;
    }
    public boolean removeByUserIdAndRoomId(Integer userId, Integer roomId) {
        return this.remove(new QueryWrapper<Player>()
                .eq("user_id", userId) // player_id 等于传入的 playerId
                .eq("room_id", roomId)); // room_id 等于传入的 roomId
    }
    public Player QueryByUserIdAndRoomId(Integer userId, Integer roomId) {
        return this.getOne(new QueryWrapper<Player>()
                .eq("user_id", userId) // player_id 等于传入的 playerId
                .eq("room_id", roomId)); // room_id 等于传入的 roomId
    }

    public int updateByUserIdAndRoomId(Player player){
        return this.playerDao.updateByUserIdAndRoomId(player);
    }
    public Player QueryByRoomPlayerIdAndRoomId(Integer roomPlayerId, Integer roomId) {
        return this.getOne(new QueryWrapper<Player>()
                .eq("room_player_id", roomPlayerId)
                .eq("room_id", roomId)); // room_id 等于传入的 roomId
    }

    public Player queryByRoomIdAndUserId(Integer roomId,Integer userId) {
        return this.getOne(new QueryWrapper<Player>()
                .eq("room_id", roomId)
                .eq("user_id", userId)); // room_id 等于传入的 roomId
    }

}
