package com.demo.http;

import com.demo.http.service.ServiceContext;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel>{
    
    private final ServiceContext ctx;
    
    public HttpChannelInitializer(ServiceContext ctx) {
        this.ctx = ctx;
    }
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpServiceDispatcher(ctx));
        
    }

}
