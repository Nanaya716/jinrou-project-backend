package com.jinrou.jinrouwerewolf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinrou.jinrouwerewolf.entity.Game.GameSettings;
import com.jinrou.jinrouwerewolf.entity.LobbyMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (LobbyMessage)表服务接口
 *
 * @author makejava
 * @since 2025-02-18 01:13:45
 */
public interface LobbyMessageService extends IService<LobbyMessage> {

    /**
     * 通过ID查询单条数据
     *
     * @param lobbyMessageId 主键
     * @return 实例对象
     */
    LobbyMessage queryById(Integer lobbyMessageId);

    /**
     * 分页查询
     *
     * @param lobbyMessage 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<LobbyMessage> queryByPage(LobbyMessage lobbyMessage, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param lobbyMessage 实例对象
     * @return 实例对象
     */
    LobbyMessage insert(LobbyMessage lobbyMessage);

    /**
     * 修改数据
     *
     * @param lobbyMessage 实例对象
     * @return 实例对象
     */
    LobbyMessage update(LobbyMessage lobbyMessage);

    /**
     * 通过主键删除数据
     *
     * @param lobbyMessageId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer lobbyMessageId);

}
