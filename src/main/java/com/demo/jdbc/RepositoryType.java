package com.demo.jdbc;

/**
 * 数据仓库类型
 * 
 * @author xingkai.zhang
 *
 */
public enum RepositoryType {

    /** 业务库 */
    SERVICE(16, 32, 4),

    /** 模板数据 */
    TEMPLATES(1, 2, 1);

    public final int defMinConn;    // 默认最小连接数
    public final int defMaxConn;    // 默认最大连接数
    public final int defPartition;  // 默认分区数量

    private RepositoryType(int minconn, int maxconn, int defPartition) {
        this.defMinConn = minconn;
        this.defMaxConn = maxconn;
        this.defPartition = defPartition;
    }

}
