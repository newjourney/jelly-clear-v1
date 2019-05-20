package com.demo.game;

import org.junit.Test;

import com.demo.game.entity.Board;
import com.demo.game.entity.JellyType;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class GameTest {

    @Test
    public void start() {
        Board board = new Board();
        int rowSize = Board.ROW_SIZE;
        int colSize = Board.COL_SIZE;
        JellyType[][] data = new JellyType[rowSize][colSize];
        for (int r = 0; r < rowSize; ++r) {
            for (int c = 0; c < colSize; ++c) {
                data[r][c] = Board.random();
            }
        }
        board.setData(data);
        
        System.out.println(board.toShowString() + "\n");
        
        board.move(0, 0, 0, 0);
        
        System.out.println(board.toShowString());
    }
    
}
