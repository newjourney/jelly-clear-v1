package com.demo.util;

import java.util.UUID;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class SessionGenerator {
    
    public static final int LENGTH = gen().length();

    public static String gen() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
}
