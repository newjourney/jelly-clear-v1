package com.demo.game.repo;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.demo.repo.BasicServiceRepo;
import com.demo.repo.DbParameter;
import com.demo.repo.Repo;

/**
 * 
 * @author xingkai.zhang
 *
 */
@Repo
public class LockRepo extends BasicServiceRepo {

    public boolean lock(String sessionId) {
        boolean result = false;
        String sql = "INSERT INTO `lock`(`SessionId`, `UpdateTime`) VALUES(?, ?);";
        Map<Integer, DbParameter> para = new HashMap<Integer, DbParameter>();
        para.put(1, new DbParameter(Types.VARCHAR, sessionId));
        para.put(2, new DbParameter(Types.TIMESTAMP, new Date()));
        result = execInsertQueryIgnoreDuplicateError(sql, para) > -1 ? true : false;
        return result;
    }

    public boolean unlock(String sessionId) {
        boolean result = false;
        String sql = "DELETE FROM `lock` WHERE `SessionId` = ?;";
        Map<Integer, DbParameter> para = new HashMap<Integer, DbParameter>();
        para.put(1, new DbParameter(Types.VARCHAR, sessionId));
        result = execNoneQuery(sql, para) > -1 ? true : false;
        return result;
    }

}
