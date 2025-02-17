package com.jinrou.jinrouwerewolf.TypeHandler;

import com.jinrou.jinrouwerewolf.entity.Identity.Identity;
import com.jinrou.jinrouwerewolf.util.GameUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ClassName IdentityToStringHandler
 * @Description MyBatis TypeHandler for storing and retrieving Identity based on its name property.
 * @Author ygt
 * @Version V2.0
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Identity.class)
public class IdentityToStringHandler implements TypeHandler<Identity> {

    /**
     * Set the name of the Identity as a parameter in the PreparedStatement.
     *
     * @param ps         PreparedStatement
     * @param i          Parameter index
     * @param parameter  Identity instance
     * @param jdbcType   JDBC type
     * @throws SQLException If any SQL error occurs
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, Identity parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setString(i, "none");
        } else {
            ps.setString(i, parameter.getName()); // 存储 Identity 的 name 属性
        }
    }

    /**
     * Retrieve the Identity from a ResultSet column by name.
     *
     * @param resultSet   ResultSet
     * @param columnName  Column name
     * @return Identity instance or null
     * @throws SQLException If any SQL error occurs
     */
    @Override
    public Identity getResult(ResultSet resultSet, String columnName) throws SQLException {
        String name = resultSet.getString(columnName);
        if (name != null) {
            return GameUtil.createIdentity(name); // 根据 name 属性创建 Identity
        }
        return null;
    }

    /**
     * Retrieve the Identity from a ResultSet column by index.
     *
     * @param resultSet   ResultSet
     * @param columnIndex Column index
     * @return Identity instance or null
     * @throws SQLException If any SQL error occurs
     */
    @Override
    public Identity getResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String name = resultSet.getString(columnIndex);
        if (name != null) {
            return GameUtil.createIdentity(name); // 根据 name 属性创建 Identity
        }
        return null;
    }

    /**
     * Retrieve the Identity from a CallableStatement by index.
     *
     * @param callableStatement CallableStatement
     * @param columnIndex       Column index
     * @return Identity instance or null
     * @throws SQLException If any SQL error occurs
     */
    @Override
    public Identity getResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String name = callableStatement.getString(columnIndex);
        if (name != null) {
            return GameUtil.createIdentity(name); // 根据 name 属性创建 Identity
        }
        return null;
    }
}
