package com.demo.game.entity;

/**
 * 各个关卡等级的初始棋盘布局
 * 
 * @author xingkai.zhang
 *
 */
public class BoardTemplate {

    private int level;
    private Board board;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

}
