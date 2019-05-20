package com.demo.game.web;

import com.demo.game.BoardTemplates;
import com.demo.game.Player;
import com.demo.game.entity.Board;
import com.demo.game.repo.BoardServiceRepo;
import com.demo.http.service.Http;
import com.demo.http.service.Request;
import com.demo.http.service.Response;
import com.demo.http.service.Service;
import com.demo.repo.RepoContext;
import com.demo.util.Log;
import com.demo.util.SessionGenerator;

@Http("start-level")
public class StartLevelService implements Service {

    @Override
    public Response service(Request req) {
        int level = req.getParamAsInt("level");
        BoardTemplates templates = BoardTemplates.instance();
        Board board = templates.get(level);
        if (board == null) {
            Log.error("Invalid param : level={}", level);
            return INVALID_PARAMS_RESP;
        }

        String sessionId = SessionGenerator.gen();
        Player player = new Player(sessionId, board);

        BoardServiceRepo repo = RepoContext.instance().get(BoardServiceRepo.class);

        boolean ret = repo.insertPlayer(player);
        if (!ret) {
            Log.error("Insert new player error, sessionId is duplicate : {}", sessionId);
            return INVALID_PARAMS_RESP;
        }
        return new Response(player.getStartResponse());
    }

}
