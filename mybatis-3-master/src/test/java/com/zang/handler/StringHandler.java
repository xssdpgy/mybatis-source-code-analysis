/**
 * hsjry.com Inc.
 * Copyright (c) 2014-2019 All Rights Reserved.
 */
package com.zang.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author xssdpgy
 * @version xssdpgy: StringHandler.java,v1.0 2019/5/21 17:54 xssdpgy Exp $$
 * @since 1.0
 */
@MappedTypes({String.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StringHandler implements TypeHandler<String>{

    private Logger log=Logger.getLogger(StringHandler.class);

    @Override
    public String getResult(ResultSet rs, String colName) throws SQLException {
        log.info("使用我的TypeHandler,ResultSet列名获取字符串");
        return rs.getString(colName);
    }

    @Override
    public String getResult(ResultSet rs, int index) throws SQLException {
        log.info("使用我的TypeHandler,ResultSet下标获取字符串");
        return rs.getString(index);
    }

    @Override
    public String getResult(CallableStatement cs, int index) throws SQLException {
        log.info("使用我的TypeHandler,CallableStatement下标获取字符串");
        return cs.getString(index);
    }

    @Override
    public void setParameter(PreparedStatement ps, int index, String value, JdbcType arg3) throws SQLException {
        log.info("使用我的TypeHandler");
        ps.setString(index, value);
    }

}
