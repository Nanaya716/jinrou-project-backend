package com.jinrou.jinrouwerewolf.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * (LobbyMessage)实体类
 *
 * @author makejava
 * @since 2025-02-18 01:13:31
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableName("lobby_message")
public class LobbyMessage implements Serializable {
    private static final long serialVersionUID = 366811804232348398L;
    /**
     * 大厅公告表
     */
    private Integer lobbyMessageId;
    /**
     * 正文内容
     */
    private String content;
    /**
     * 发布者的用户id
     */
    private Integer publishUserId;
    /**
     * 发布时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;


    public Integer getLobbyMessageId() {
        return lobbyMessageId;
    }

    public void setLobbyMessageId(Integer lobbyMessageId) {
        this.lobbyMessageId = lobbyMessageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(Integer publishUserId) {
        this.publishUserId = publishUserId;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

}

