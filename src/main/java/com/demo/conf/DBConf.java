package com.demo.conf;

import com.demo.conf.ConfContext.PropertyGetter;
import com.demo.jdbc.RepositoryType;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class DBConf {

    public final String host;
    public final int    port;
    public final String name;
    public final String user;
    public final String pass;

    public final int minconn;
    public final int maxconn;
    public final int partition;

    private DBConf(String host, int port, String name, String user, String pass, int minconn, int maxconn, int partition) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.user = user;
        this.pass = pass;
        this.minconn = minconn;
        this.maxconn = maxconn;
        this.partition = partition;
    }
    
    public static DBConf parseFrom(RepositoryType type, PropertyGetter valGetter) {
        String perfix = type.name().toLowerCase() + ".db.";
        return new DBConf(
                valGetter.get(perfix + "host"),
                valGetter.getAsInt(perfix + "port"),
                valGetter.get(perfix + "name"),
                valGetter.get(perfix + "user"),
                valGetter.get(perfix + "pass"),
                valGetter.getAsInt(perfix + "minconn", type.defMinConn),
                valGetter.getAsInt(perfix + "minconn", type.defMaxConn),
                valGetter.getAsInt(perfix + "partition", type.defPartition));
    }

    public static interface ValGetter {
        public String get(String name);
        
        public default String get(String name, String def) {
            String value = get(name);
            return value == null ? def : value;
        }
        public default int getAsInt(String name) {
            return Integer.parseInt(get(name));
        }
        public default int getAsInt(String name, int def) {
            String value = get(name);
            return value == null ? def : Integer.parseInt(value);
        }
    }

}
