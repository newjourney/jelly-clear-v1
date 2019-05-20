package com.demo.game;

import java.util.HashMap;
import java.util.Map;

import com.demo.game.entity.Board;
import com.demo.game.repo.BoardTemplateRepo;
import com.demo.util.Log;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class BoardTemplates {

    private BoardTemplateRepo repo;

    private Map<Integer, Board> boardsByLevel;

    private BoardTemplates() {
        repo = new BoardTemplateRepo();
    }

    private static class Holder {
        private static final BoardTemplates INSTANCE = new BoardTemplates();
    }

    public static BoardTemplates instance() {
        return Holder.INSTANCE;
    }

    public void init() {
        boardsByLevel = new HashMap<>();
        repo.fetchAllBoardTemplates().forEach(template -> {
            boardsByLevel.put(template.getLevel(), template.getBoard());
            Log.debug("{} : {}", template.getLevel(), template.getBoard());
        });
    }
    
    public Board get(int level) {
        Board template = boardsByLevel.get(level);
        return template != null ? template.cloneMe() : null;
    }

}
