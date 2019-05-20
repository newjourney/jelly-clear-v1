package com.demo.util;

import java.util.Random;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class RandomUtil {

    private static Random random = new Random();

    private static Random getR() {
        return random;
    }

    /**
     * 开区间
     * 
     * @return
     */
    public static int randomOpenNum(int max) {
        return randomOpenNum(0, max);
    }

    /**
     * 开区间 [min, max)
     * 
     * @param min
     * @param max
     * @return
     */
    public static int randomOpenNum(int min, int max) {
        int result = getR().nextInt((max - min) << 5);
        return (result >> 5) + min;
    }

    /**
     * 闭区间
     * 
     * @param max
     * @return [0, max]
     */
    public static int randomCloseNum(int max) {
        return randomCloseNum(0, max);
    }

    /**
     * 闭区间
     * 
     * @param min
     * @param max
     * @return [min, max]
     */
    public static int randomCloseNum(int min, int max) {
        int result = getR().nextInt((max - min + 1) << 5);
        return (result >> 5) + min;
    }
}
