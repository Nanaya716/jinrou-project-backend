package com.jinrou.jinrouwerewolf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jinrou.jinrouwerewolf.entity.Game.Room;
import com.jinrou.jinrouwerewolf.entity.LobbyMessage;
import com.jinrou.jinrouwerewolf.entity.Result;
import com.jinrou.jinrouwerewolf.service.LobbyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * (LobbyMessage)表控制层
 *
 * @author makejava
 * @since 2025-02-18 01:13:31
 */
@RestController
@RequestMapping("/api/lobbyMessage")
public class LobbyMessageController {

    @Autowired
    private LobbyMessageService lobbyMessageService;
    @PostMapping("/getLobbyMessages")
    public Result getLobbyMessages() {
        QueryWrapper<LobbyMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("publish_time")
                .last("LIMIT 10");  // 按照 publish_time 字段升序排列
        List<LobbyMessage> messages = lobbyMessageService.list(queryWrapper);

        return Result.success(messages);
    }

    /**
     * 分页查询
     *
     * @param lobbyMessage 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Page<LobbyMessage>> queryByPage(LobbyMessage lobbyMessage, PageRequest pageRequest) {
        return ResponseEntity.ok(this.lobbyMessageService.queryByPage(lobbyMessage, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<LobbyMessage> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.lobbyMessageService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param lobbyMessage 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<LobbyMessage> add(LobbyMessage lobbyMessage) {
        return ResponseEntity.ok(this.lobbyMessageService.insert(lobbyMessage));
    }

    /**
     * 编辑数据
     *
     * @param lobbyMessage 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<LobbyMessage> edit(LobbyMessage lobbyMessage) {
        return ResponseEntity.ok(this.lobbyMessageService.update(lobbyMessage));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.lobbyMessageService.deleteById(id));
    }

}

