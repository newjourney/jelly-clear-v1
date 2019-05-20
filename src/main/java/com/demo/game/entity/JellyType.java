package com.demo.game.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 果冻类型
 * 
 * @author xingkai.zhang
 *
 */
public enum JellyType {

    /** 临时状态 */
    TMP('-'),
    /** 普通果冻 */
    COMMON('B'),
    /** 横炸弹 */
    HORIZONTAL_BOMB('H', (row, col, jellys) -> {
        int size = jellys[row].length;
        for (int c = 0; c < size; ++c) {
            JellyType data = jellys[row][c];
            jellys[row][c] = TMP; // 先置空, 避免循环调用
            if (c != col) { // 排除自己
                data.clear(row, c, jellys);
            }
        }
    }),
    /** 竖炸弹 */
    VERTICAL_BOMB('V', (row, col, jellys) -> {
        int size = jellys.length;
        for (int r = 0; r < size; ++r) {
            JellyType data = jellys[r][col];
            jellys[r][col] = TMP;
            if (r != row) {
                data.clear(r, col, jellys);
            }
        }
    }),
    /** 方炸弹 */
    SQUARE_BOMB('S', (row, col, jellys) -> {
        int maxRow = jellys[0].length;
        int maxCol = jellys.length;
        for (int r = row - 1; r <= row + 1; ++r) {
            for (int c = col - 1; c <= col + 1; ++c) {
                if (r >= 0 && r < maxRow && c >= 0 && c < maxCol) {
                    JellyType data = jellys[r][c];
                    jellys[r][c] = TMP;
                    if (r != row || c != col) {
                        data.clear(row, col, jellys);
                    }
                }
            }
        }
    })
    ;

    public final char flag;
    private final ClearHandler clearHandler;

    private static Map<Character, JellyType> flagMap = new HashMap<>();

    private JellyType(char flag) {
        this(flag, (r, c, j) -> {
        });
    }

    private JellyType(char flag, ClearHandler clearHandler) {
        this.flag = flag;
        this.clearHandler = clearHandler;
    }

    static {
        for (JellyType type : values()) {
            flagMap.put(type.flag, type);
        }
    }

    public static JellyType parseFrom(char flag) {
        return flagMap.get(flag);
    }
    
    public void clear(int cow, int rol, JellyType[][] data) {
        clearHandler.clear(cow, rol, data);
    }

    public static interface ClearHandler {
        void clear(int cow, int rol, JellyType[][] data);
    }
    
}
