package com.demo.game;

import com.demo.game.entity.Board;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class Player {

    private String sessionId;
    private Board board;
    
    public Player() {
    }

    public Player(String sessionId, Board board) {
        this.sessionId = sessionId;
        this.board = board;
    }
    
    public String move(int row0, int col0, int row1, int col1) {
        board.move(row0, col0, row1, col1);
        return board.toShowString();
    }
    
    public String getStartResponse() {
        StringBuilder ret = new StringBuilder();
        ret.append(sessionId).append("\n");
        ret.append(board.toShowString());
        return ret.toString();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

}
