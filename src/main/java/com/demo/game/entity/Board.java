package com.demo.game.entity;

import com.demo.util.RandomUtil;

/**
 * 棋盘
 * 
 * @author xingkai.zhang
 *
 */
public class Board {

    public static final int ROW_SIZE = 8;
    public static final int COL_SIZE = 8;

    private JellyType[][] data;

    public Board() {
        this.data = new JellyType[ROW_SIZE][COL_SIZE];
    }

    public void move(int row0, int col0, int row1, int col1) {
        // 消除
        for (int row = row0; row <= row1; ++row) {
            for (int col = col0; col <= col1; ++col) {
                JellyType type = data[row][col];
                type.clear(row, col, data);
            }
        }

        // 补全
        for (int row = ROW_SIZE - 1; row >= 0; --row) {
            for (int col = COL_SIZE - 1; col >= 0; --col) {
                if (data[row][col] == JellyType.TMP) {
                    data[row][col] = fill(row, col);
                }
            }
        }
    }

    private JellyType fill(int row, int col) {
        if (row < 0) {
            return random(); // 到头了, 随机
        }
        if (data[row][col] == JellyType.TMP) {
            return fill(row - 1, col); // 向上寻找
        } else {
            JellyType ret = data[row][col];
            data[row][col] = JellyType.TMP; // 置空
            return ret;
        }
    }

    public Board cloneMe() {
        Board clone = new Board();
        for (int r = 0; r < ROW_SIZE; ++r) {
            for (int c = 0; c < COL_SIZE; ++c) {
                clone.data[r][c] = data[r][c];
            }
        }
        return clone;
    }

    /**
     * @param str
     * @return self
     */
    public Board parseFrom(String str) {
        int rowSize = ROW_SIZE;
        int colSize = COL_SIZE;
        if (str.length() != rowSize * colSize) {
            throw new IllegalArgumentException("Illegal string length for board : " + str.length());
        }
        int rowIdx = 0;
        int colIdx = 0;
        for (char c : str.toCharArray()) {
            if (colIdx >= colSize) {
                colIdx = 0;
                rowIdx++;
            }
            data[rowIdx][colIdx++] = JellyType.parseFrom(c);
        }
        return this;
    }

    public JellyType[][] getData() {
        return data;
    }

    public void setData(JellyType[][] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return toString(this.data, false);
    }

    public String toShowString() {
        return toString(this.data, true);
    }

    public static String toString(JellyType[][] data, boolean forShow) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < ROW_SIZE; ++r) {
            for (int c = 0; c < COL_SIZE; ++c) {
                JellyType d = data[r][c];
                sb.append(d.flag);
            }
            if (forShow && r < ROW_SIZE - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static JellyType random() {
        JellyType[] all = JellyType.values();
        return all[RandomUtil.randomOpenNum(1, all.length)]; // 不从0开始是为了过滤掉TMP
    }

    public static void main(String[] args) {
        JellyType testType = JellyType.HORIZONTAL_BOMB;
        int tr = -1, tc = -1;
        Board board = new Board();
        for (int r = 0; r < ROW_SIZE; ++r) {
            for (int c = 0; c < COL_SIZE; ++c) {
                JellyType jelly = random();
                board.data[r][c] = jelly;
                if (r >= 2 && r >= 2 && tr == -1 && jelly == testType) {
                    tr = r;
                    tc = c;
                }
            }
        }
        System.out.println(board.toShowString());
        System.out.println("\ntarget : " + tr + "," + tc + "\n");
        board.move(tr, tc, tr, tc);
        System.out.println();
        System.out.println(board.toShowString());
    }

}
