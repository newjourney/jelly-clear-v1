package com.demo.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.conf.DBConf;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * 基于BoneCP实现的数据库连接池
 * 
 * @author xingkai.zhang
 *
 */
public class JDBCPool {

    private static final String DB_URL = "jdbc:mysql://%s:%s/%s?characterEncoding=utf-8&autoReconnect=true";
    private static final Logger logger = LoggerFactory.getLogger("JDBCPool");

    private BoneCP connectionPool = null;
    private BoneCPConfig config;

    public JDBCPool(DBConf conf) throws Exception {
        this(genDBUrl(conf), conf.user, conf.pass, conf.minconn, conf.maxconn, conf.partition);
    }

    public JDBCPool(String dbConnUrl, String dbUserName, String dbPassWord, int minConn, int maxConn, int partition)
            throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        config = new BoneCPConfig();
        config.setJdbcUrl(dbConnUrl);
        config.setUsername(dbUserName);
        config.setPassword(dbPassWord);

        // 分区数 ，默认值2，最小1，推荐3-4，视应用而定
        config.setPartitionCount(partition);
        // 每个分区最小的连接数
        config.setMinConnectionsPerPartition(minConn);
        // 每个分区最大的连接数
        config.setMaxConnectionsPerPartition(maxConn);
        // 缓存prepared statements的大小，默认值：0
        config.setStatementsCacheSize(50);
        // 每次去拿数据库连接的时候一次性要拿几个,默认值：2
        config.setAcquireIncrement(5);
        // 设置重新获取连接的次数间隔时间。这个参数默认为7000，单位：毫秒。如果小于等于0，BoneCP将设置为1000
        config.setAcquireRetryDelayInMs(3000);
        // 设置重新获取连接的次数。这个参数默认为5
        config.setAcquireRetryAttempts(5);
        // 设置connection的空闲存活时间。这个参数默认为60，单位：分钟。设置为0该功能失效。通过ConnectionTesterThread观察每个分区中的connection，如果这个connection距离最后使用的时间大于这个参数就会被清除。
        config.setIdleMaxAgeInMinutes(10);
        // 设置事务回放功能。这个参数默认为false
        config.setTransactionRecoveryEnabled(true);
        // 通过ConnectionTesterThread观察每个分区中的connection，
        // 如果这个connection距离最后使用的时间大于这个参数并且距离上一次测试的时间大于这个参数就会向数据库发送一条测试语句，如果执行失败则将这个connection清除。
        config.setIdleConnectionTestPeriodInMinutes(10);
        // 置获取connection超时的时间。这个参数默认为Long.MAX_VALUE;单位：毫秒。在调用getConnection获取connection时，获取时间超过了这个参数，就视为超时并报异常
        config.setConnectionTimeoutInMs(3000);

        connectionPool = new BoneCP(config);
    }

    private static String genDBUrl(DBConf conf) {
        return String.format(DB_URL, conf.host, conf.port, conf.name);
    }

    public void shutdown() {
        if (connectionPool != null) {
            connectionPool.close();
        }
    }

    public String getState() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("total:" + connectionPool.getTotalCreatedConnections()).append(",");
            sb.append("used:" + connectionPool.getTotalLeased()).append(",");
            sb.append("free:" + connectionPool.getTotalFree());
        } catch (Exception e) {
            logger.error("Pool Error", e);
        }
        return sb.toString();
    }

    public BoneCP getBoneCp() {
        return connectionPool;
    }

    /**
     * 从连接池获得一个可用连接.<br>
     * 如果没有空闲的连接且当前连接数小于最大连接 数限制,则创建新连接. <br>
     * 如原来登记为可用的连接不再有效,则从向量删除之,然后递归调用自己以尝试新的可用连接.
     */
    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            logger.error("获取数据库连接失败", e);
            return null;
        }
    }

    /**
     * 获取当前总连接数
     */
    public int getCurConns() {
        if (connectionPool != null) {
            return connectionPool.getTotalCreatedConnections();
        }
        logger.error("connectionPool is null");
        return 0;
    }

}