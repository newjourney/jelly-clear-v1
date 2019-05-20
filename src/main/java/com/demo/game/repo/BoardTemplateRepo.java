package com.demo.game.repo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo.game.entity.Board;
import com.demo.game.entity.BoardTemplate;
import com.demo.repo.BasicTemplatesRepo;
import com.demo.repo.DbParameter;
import com.demo.repo.Repo;
import com.demo.util.Log;

/**
 * 
 * @author xingkai.zhang
 *
 */
@Repo
public class BoardTemplateRepo extends BasicTemplatesRepo {

    public List<BoardTemplate> fetchAllBoardTemplates() {
        String sqlText = "select * from board;";
        Map<Integer, DbParameter> para = new HashMap<Integer, DbParameter>();
        PreparedStatement pstmt = execQuery(sqlText, para);
        ResultSet rs = null;
        List<BoardTemplate> infos = null;
        if (pstmt != null) {
            infos = new ArrayList<>();
            try {
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    BoardTemplate tempInfo = new BoardTemplate();
                    tempInfo.setLevel(rs.getInt("Level"));
                    tempInfo.setBoard(new Board().parseFrom(rs.getString("Board")));
                    infos.add(tempInfo);
                }
            } catch (SQLException e) {
                Log.error("执行出错" + sqlText, e);
            } finally {
                closeConn(pstmt, rs);
            }
        }
        return infos;
    }

}
