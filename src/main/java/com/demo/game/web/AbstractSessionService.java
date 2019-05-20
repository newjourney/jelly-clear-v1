package com.demo.game.web;

import com.demo.game.repo.LockRepo;
import com.demo.http.service.Request;
import com.demo.http.service.Response;
import com.demo.http.service.Service;
import com.demo.repo.RepoContext;
import com.demo.util.Log;

/**
 * 通过数据库实现简单的分布式锁。 
 * 牺牲一定的可用性，保证一致性。
 * 
 * @author xingkai.zhang
 *
 */
public abstract class AbstractSessionService implements Service {

    @Override
    public Response service(Request req) {
        String sessionId = req.getParam("sessionId");
        if (sessionId == null) {
            return INVALID_PARAMS_RESP;
        }
        if (lock(sessionId)) {
            Response resp = INVALID_PARAMS_RESP;
            try {
                resp = exec(sessionId, req);
            } finally {
                unlock(sessionId);
            }
            return resp;
        } else {
            Log.error("Fail to get lock : {}", sessionId);
            return INVALID_PARAMS_RESP;
        }
    }

    protected abstract Response exec(String sessionId, Request req);

    private boolean lock(String sessionId) {
        LockRepo lockRepo = RepoContext.instance().get(LockRepo.class);
        int tryCnt = 0;
        for (;;) {
            if (lockRepo.lock(sessionId)) {
                return true;
            }
            if (++tryCnt == 5) { // 自旋n次
                return false;
            }
        }
    }

    private void unlock(String sessionId) {
        LockRepo lockRepo = RepoContext.instance().get(LockRepo.class);
        lockRepo.unlock(sessionId);
    }

}
