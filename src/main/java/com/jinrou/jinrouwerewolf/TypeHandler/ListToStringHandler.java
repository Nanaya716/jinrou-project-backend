package com.jinrou.jinrouwerewolf.TypeHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.type.*;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName ListToStringHandler
 * @Description mybatis list转换为String 相互转换工具类
 * @Author ygt
 * @Date 2021/3/3 14:49
 * @Version V1.0
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(List.class)
public class ListToStringHandler implements TypeHandler<List<String>> {

    /**
     * @param ps
     * @param i
     * @param strings
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> strings, JdbcType jdbcType) throws SQLException {
        StringBuffer sb = new StringBuffer();
        for (String temp : strings) {
            sb.append(temp).append(",");
        }
        ps.setString(i, sb.toString());
    }

    @Override
    public List<String> getResult(ResultSet resultSet, String columnName) throws SQLException {
        String s = resultSet.getString(columnName);
        if (s != null && !s.trim().isEmpty()) {
            return Arrays.asList(s.split(","));
        }
        return Collections.emptyList(); // 如果是空字符串或null，则返回空的List

    }

    @Override
    public List<String> getResult(ResultSet resultSet, int i) throws SQLException {
        String s = resultSet.getString(i);
        if (s != null && !s.trim().isEmpty()) {
            return Arrays.asList(s.split(","));
        }
        return Collections.emptyList(); // 如果是空字符串或null，则返回空的List
    }

    @Override
    public List<String> getResult(CallableStatement callableStatement, int i) throws SQLException {
        String s = callableStatement.getString(i);
        if (s != null && !s.trim().isEmpty()) {
            return Arrays.asList(s.split(","));
        }
        return Collections.emptyList(); // 如果是空字符串或null，则返回空的List
    }
}
