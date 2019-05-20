package com.demo.util;

import java.util.UUID;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class SessionGenerator {

    public static String gen() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static void main(String[] args) {
        System.out.println(gen().length());
    }
}
