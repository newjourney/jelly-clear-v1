package com.demo.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfContext {

    private static final class Holder {
        private static final ConfContext INSTANCE = new ConfContext();
    }

    private final PropertyGetter propertyGetter;
    private Properties properties;

    private ConfContext() {
        this.propertyGetter = key -> properties.getProperty(key);
    }
    
    public static ConfContext instance() {
        return Holder.INSTANCE;
    }

    public void init() {
        String confdir = System.getProperty("conf.dir", "");
        String confPath = Paths.get(confdir, "conf.properties").toString();
        try {
            properties = new Properties();
            properties.load(getResourceAsStream(confPath));
        } catch (Exception e) {
            throw new Error("Load config files error", e);
        }
    }

    public PropertyGetter propertyGetter() {
        return this.propertyGetter;
    }

    private static InputStream getResourceAsStream(String file) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (in == null)
            in = ClassLoader.getSystemClassLoader().getResourceAsStream(file);
        if (in == null)
            in = new FileInputStream(file);
        return in;
    }

    public static interface PropertyGetter {
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
