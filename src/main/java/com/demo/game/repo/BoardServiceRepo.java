package com.demo.game.repo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.demo.game.Player;
import com.demo.game.entity.Board;
import com.demo.repo.BasicServiceRepo;
import com.demo.repo.DbParameter;
import com.demo.repo.Repo;
import com.demo.util.Log;

/**
 * 
 * @author xingkai.zhang
 *
 */
@Repo
public class BoardServiceRepo extends BasicServiceRepo {

    public boolean insertPlayer(Player player) {
        boolean result = false;
        String sql = "INSERT INTO `player`(`SessionId`, `Board`) VALUES(?, ?);";
        Map<Integer, DbParameter> para = new HashMap<Integer, DbParameter>();
        para.put(1, new DbParameter(Types.VARCHAR, player.getSessionId()));
        para.put(2, new DbParameter(Types.VARCHAR, player.getBoard().toString()));
        result = execNoneQuery(sql, para) > -1 ? true : false;
        return result;

    }

    public Player fetchPlayer(String sessionId) {
        String sqlText = "SELECT * FROM `player` WHERE `SessionId` = ?;";
        Map<Integer, DbParameter> para = new HashMap<Integer, DbParameter>();
        para.put(1, new DbParameter(Types.VARCHAR, sessionId));
        PreparedStatement pstmt = execQuery(sqlText, para);
        ResultSet rs = null;
        Player player = null;
        if (pstmt != null) {
            try {
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    player = new Player(sessionId, new Board().parseFrom(rs.getString("Board")));
                }
            } catch (SQLException e) {
                Log.error("执行出错" + sqlText, e);
            } finally {
                closeConn(pstmt, rs);
            }
        }
        return player;
    }

}
