package com.demo.game.web;

import com.demo.game.Player;
import com.demo.game.entity.Board;
import com.demo.game.repo.BoardServiceRepo;
import com.demo.http.service.Http;
import com.demo.http.service.Request;
import com.demo.http.service.Response;
import com.demo.repo.RepoContext;
import com.demo.util.Log;

@Http("move")
public class MoveService extends AbstractSessionService {

    @Override
    protected Response exec(String sessionId, Request req) {
        int row0 = req.getParamAsInt("row0");
        int col0 = req.getParamAsInt("col0");
        int row1 = req.getParamAsInt("row1");
        int col1 = req.getParamAsInt("col1");
        if (!isLegalParam(row0, col0, row1, col1)) {
            return INVALID_PARAMS_RESP;
        }
        
        BoardServiceRepo repo = RepoContext.instance().get(BoardServiceRepo.class);
        Player player = repo.fetchPlayer(sessionId);
        if (player == null) {
            Log.error("Player not found, sessionId={}", sessionId);
            return INVALID_PARAMS_RESP;
        }
        String ret = player.move(row0, col0, row1, col1);
        return new Response(ret);
    }

    private boolean isLegalParam(int row0, int col0, int row1, int col1) {
        int colSize = Board.COL_SIZE;
        int rowSize = Board.ROW_SIZE;
        return row0 >= 0 && row0 < rowSize
                && col0 >= 0 && col0 < colSize
                && row1 >= 0 && row1 < rowSize
                && col1 >= 0 && col1 < colSize
                && row0 <= row1
                && col0 <= col1;
    }

}
