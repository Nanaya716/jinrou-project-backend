package com.jinrou.jinrouwerewolf.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinrou.jinrouwerewolf.entity.LobbyMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (LobbyMessage)表数据库访问层
 *
 * @author makejava
 * @since 2025-02-18 01:13:31
 */
@Mapper
@Repository
public interface LobbyMessageDao extends BaseMapper<LobbyMessage> {

    /**
     * 通过ID查询单条数据
     *
     * @param lobbyMessageId 主键
     * @return 实例对象
     */
    LobbyMessage queryById(Integer lobbyMessageId);

    /**
     * 查询指定行数据
     *
     * @param lobbyMessage 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<LobbyMessage> queryAllByLimit(LobbyMessage lobbyMessage, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param lobbyMessage 查询条件
     * @return 总行数
     */
    long count(LobbyMessage lobbyMessage);

    /**
     * 新增数据
     *
     * @param lobbyMessage 实例对象
     * @return 影响行数
     */
    int insert(LobbyMessage lobbyMessage);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<LobbyMessage> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<LobbyMessage> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<LobbyMessage> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<LobbyMessage> entities);

    /**
     * 修改数据
     *
     * @param lobbyMessage 实例对象
     * @return 影响行数
     */
    int update(LobbyMessage lobbyMessage);

    /**
     * 通过主键删除数据
     *
     * @param lobbyMessageId 主键
     * @return 影响行数
     */
    int deleteById(Integer lobbyMessageId);

}

