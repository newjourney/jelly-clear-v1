package com.demo.jdbc;

import java.sql.Connection;
import java.util.EnumMap;

import com.demo.conf.ConfContext;
import com.demo.conf.ConfContext.PropertyGetter;
import com.demo.util.Log;
import com.demo.conf.DBConf;

/**
 * 数据库连接池管理类
 * 
 * @author xingkai.zhang
 *
 */
public class JdbcPoolContext {

    public static final String DB_URL = "jdbc:mysql://%s:%s/%s?characterEncoding=utf-8&autoReconnect=true";

    private EnumMap<RepositoryType, JDBCPool> pools = new EnumMap<>(RepositoryType.class);

    private JdbcPoolContext() {
    }

    private static class Holder {
        private static JdbcPoolContext INSTANCE = new JdbcPoolContext();
    }

    public static JdbcPoolContext instance() {
        return Holder.INSTANCE;
    }

    public void init() {
        PropertyGetter valGetter = ConfContext.instance().propertyGetter();
        for (RepositoryType type : RepositoryType.values()) {
            try {
                pools.put(type, new JDBCPool(DBConf.parseFrom(type, valGetter)));
            } catch (Exception e) {
                throw new Error("Init JDBCPool error, type : " + type, e);
            }
        }
    }

    public void shutdown() {
        pools.values().forEach(pool -> {
            if (pool != null) {
                pool.shutdown();
                pool = null;
            }
        });
        Log.info("数据库连接池关闭成功");
    }

    public Connection getConn(RepositoryType type) {
        JDBCPool pool = pools.get(type);
        return pool.getConnection();
    }

}
