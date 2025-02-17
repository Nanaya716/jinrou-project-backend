package com.jinrou.jinrouwerewolf.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinrou.jinrouwerewolf.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (User)表数据库访问层
 *
 * @author makejava
 * @since 2024-07-08 07:41:57
 */
@Mapper
@Repository
public interface UserDao extends BaseMapper<User> {

}

