package com.demo.repo;

public interface ErrorMsg {

	public static final String FORMAT = "[DataBase] 提示消息:%s; 错误描述:%s; 当前状态:%s";

	public static final String CONNECT_CLOSE_ERROR = "关闭数据库出错";

	public static final String SQL_ERROR = "当前Sql语句出错";

}
