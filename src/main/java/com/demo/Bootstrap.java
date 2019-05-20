package com.demo;

import com.demo.conf.ConfContext;
import com.demo.game.BoardTemplates;
import com.demo.http.HttpServer;
import com.demo.http.service.ServiceContext;
import com.demo.jdbc.JdbcPoolContext;

public class Bootstrap {

    private static HttpServer httpServer;

    public static void stop() {
        if (httpServer != null) {
            httpServer.stop();
        }
    }

    public static void main(String[] args) {
        ConfContext.instance().init();
        ServiceContext.instance().init();
        JdbcPoolContext.instance().init();
        
        BoardTemplates.instance().init();

        httpServer = new HttpServer();
        int port = ConfContext.instance().propertyGetter().getAsInt("http.port", 8080);
        httpServer.start(8, port, ServiceContext.instance());
    }
}
