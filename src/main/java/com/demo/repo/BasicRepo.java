package com.demo.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.demo.jdbc.JdbcPoolContext;
import com.demo.jdbc.RepositoryType;
import com.demo.util.Log;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * 
 * @author xingkai.zhang
 *
 */
public abstract class BasicRepo {

    protected abstract RepositoryType repoType();

    protected Connection openConn() {
        return JdbcPoolContext.instance().getConn(repoType());
    }

    protected int execNoneQuery(String sql) {
        return execNoneQuery(sql, null);
    }

    protected int execNoneQuery(String sql, Map<Integer, DbParameter> params) {
        int result = -1;
        Connection conn = openConn();
        if (conn == null) {
            return result;
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            prepareCommand(pstmt, params);
            result = pstmt.executeUpdate();
        } catch (Exception ex) {
            String msg = String.format(ErrorMsg.FORMAT, ErrorMsg.SQL_ERROR, ex.getMessage(), "调用Sql语句" + sql + "出错");
            Log.error(msg, ex);
        } finally {
            closeConn(conn, pstmt);
        }
        return result;
    }

    protected int execInsertQueryIgnoreDuplicateError(String sql, Map<Integer, DbParameter> params) {
        int result = -1;
        Connection conn = openConn();
        if (conn == null) {
            return result;
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            prepareCommand(pstmt, params);
            result = pstmt.executeUpdate();
        } catch (MySQLIntegrityConstraintViolationException ex) {
            String message = ex.getMessage();
            // ignore duplicate error
            if (message == null || !message.contains("Duplicate entry")) {
                String msg = String.format(ErrorMsg.FORMAT, ErrorMsg.SQL_ERROR, message, "调用Sql语句" + sql + "出错");
                Log.error(msg, ex);
            }
        } catch (Exception ex) {
            String msg = String.format(ErrorMsg.FORMAT, ErrorMsg.SQL_ERROR, ex.getMessage(), "调用Sql语句" + sql + "出错");
            Log.error(msg, ex);
        } finally {
            closeConn(conn, pstmt);
        }
        return result;
    }

    protected PreparedStatement execQuery(String sql) {
        return execQuery(sql, null);
    }

    protected PreparedStatement execQuery(String sql, Map<Integer, DbParameter> params) {
        Connection conn = openConn();
        if (conn == null) {
            return null;
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            prepareCommand(pstmt, params);
        } catch (Exception ex) {
            String msg = String.format(ErrorMsg.FORMAT, ErrorMsg.SQL_ERROR, ex.getMessage(), "调用Sql语句" + sql + "出错");
            Log.error(msg, ex);
            closeConn(conn, pstmt);
        }
        return pstmt;
    }

    protected int execLastId(String sql, Map<Integer, DbParameter> params) {
        int result = -1;
        Connection conn = openConn();
        if (conn == null) {
            return result;
        }
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepareCommand(pstmt, params);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception ex) {
            String msg = "获取自增id出错";
            Log.error(msg, ex);
            result = -1;
        } finally {
            closeConn(conn, pstmt, rs);
        }
        return result;
    }

    protected int execLastId(String sql) {
        return execLastId(sql, null);
    }

    protected boolean sqlBatch(String tableName, List<String> sqlComm) {
        if (sqlComm == null || sqlComm.size() == 0)
            return true;

        Connection conn = openConn();
        if (conn == null) {
            return false;
        }
        Statement stmt = null;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            for (String sqlString : sqlComm) {
                stmt.addBatch(sqlString);
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            String msg = "批处理执行SQL语句出错";
            Log.error(msg, e);
        } finally {
            closeConn(conn, stmt);
        }
        return false;
    }

    protected boolean runProcePrepared(String procName) {
        boolean result = false;
        Connection conn = openConn();
        if (conn == null) {
            return result;
        }
        String sql = ProcedureName(procName, 0);
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sql);
            statement.execute();
            result = true;
        } catch (SQLException e) {
            String msg = String.format("批处理执行存储过程%s出错", procName);
            Log.error(msg, e);
            result = false;
        } finally {
            closeConn(conn, statement);
        }
        return result;
    }

    protected int execCountQuery(String sql, Map<Integer, DbParameter> params) {
        int result = -1;
        Connection conn = openConn();
        if (conn == null) {
            return result;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            prepareCommand(pstmt, params);
            rs = pstmt.executeQuery();
            if ((rs != null) && (rs.next())) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            String msg = "统计Count出错, 调用Sql语句 : " + sql + "出错";
            Log.error(msg, e);
            result = -1;
        } finally {
            closeConn(conn, pstmt, rs);
        }
        return result;
    }

    protected String getDbName() {
        Connection conn = openConn();
        if (conn == null) {
            return "";
        }
        String temp = "";
        try {
            temp = conn.getCatalog();
        } catch (SQLException e) {
            Log.error("获取数据库名出错", e);
        } finally {
            closeConn(conn);
        }
        return temp;

    }

    private void closeConn(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            Log.error(ErrorMsg.CONNECT_CLOSE_ERROR, e);
        }
    }

    private void closeConn(Connection conn, PreparedStatement pstmt) {
        try {
            if (pstmt != null) {
                pstmt.clearParameters();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            Log.error(ErrorMsg.CONNECT_CLOSE_ERROR, e);
        }
        closeConn(conn);
    }

    private void closeConn(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            Log.error(ErrorMsg.CONNECT_CLOSE_ERROR, e);
        }
        closeConn(conn, pstmt);
    }

    private void closeConn(Connection conn, Statement statement) {
        if (statement != null) {
            try {
                statement.clearBatch();
                statement.close();
            } catch (SQLException e) {
                Log.error(ErrorMsg.CONNECT_CLOSE_ERROR, e);
            }
        }
        closeConn(conn);
    }

    protected void closeConn(PreparedStatement pstmt, ResultSet rs) {
        try {
            if (pstmt != null) {
                Connection conn = pstmt.getConnection();
                closeConn(conn, pstmt, rs);
            }
        } catch (SQLException e) {
            Log.error("关闭db连接时异常", e);
        }
    }

    private void prepareCommand(PreparedStatement pstmt, Map<Integer, DbParameter> parms) throws SQLException {
        if (pstmt == null || parms == null)
            return;
        for (Map.Entry<Integer, DbParameter> entry : parms.entrySet()) {
            pstmt.setObject(entry.getKey(), entry.getValue().getResult());
        }
    }

    protected String getPage(String tableName, String fields, int pageSize, int pageNow, String orderString,
            String paraStr) {
        int beginRow = 0;
        if (pageSize <= 0)
            pageSize = 1;
        if (pageNow <= 0)
            pageNow = 1;
        String limitString;
        beginRow = (pageNow - 1) * pageSize;
        limitString = " LIMIT " + beginRow + ", " + pageSize;
        String sqlString = "SELECT " + fields + " FROM " + tableName + "  Where " + paraStr + "  Order By "
                + orderString + limitString + ";";
        return sqlString;
    }

    protected String getPageByBeginRow(String tableName, String fields, int pageSize, int beginRow, String orderString,
            String paraStr) {
        if (pageSize <= 0)
            pageSize = 1;
        if (beginRow < 0)
            beginRow = 1;
        String limitString;
        limitString = " LIMIT " + beginRow + ", " + pageSize;
        String sqlString = "SELECT " + fields + " FROM " + tableName + "  Where " + paraStr + "  Order By "
                + orderString + limitString + ";";
        return sqlString;
    }

    private String ProcedureName(String procName, int paraCount) {
        int paramCount = paraCount; // 参数个数
        String questionMark = ""; // 存放问号的字符串. 如?或?,?...
        // 设置问号字符串, 以逗号隔开
        for (int i = 0; i < paramCount; i++) {
            questionMark += "?,";
        }
        if (questionMark != "") {
            questionMark = questionMark.substring(0, questionMark.length() - 1);
        }
        return "{call  " + procName + "(" + questionMark + ")}";
    }
}
