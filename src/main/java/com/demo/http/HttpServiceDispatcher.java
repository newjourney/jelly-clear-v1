package com.demo.http;

import java.net.InetSocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.http.service.Request;
import com.demo.http.service.Response;
import com.demo.http.service.Service;
import com.demo.http.service.ServiceContext;
import com.demo.http.service.ServiceContext.ServiceInvoker;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class HttpServiceDispatcher extends SimpleChannelInboundHandler<Object> {
    
    private static final Logger logger = LoggerFactory.getLogger("HttpServiceDispatcher");
    
    private final ServiceContext serviceCtx;
    
    public HttpServiceDispatcher(ServiceContext serviceCtx) {
        this.serviceCtx = serviceCtx;
    }
    
    private Request req;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            this.req = new Request(((InetSocketAddress)(ctx.channel().remoteAddress())).getAddress(), request);
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            req.appendDecoderResult(request.getDecoderResult());
        }

       if (msg instanceof HttpContent) {
           req.appendContent((HttpContent) msg);
           
           if (msg instanceof LastHttpContent) {
               service(ctx);
           }
       }
   }

    private void service(ChannelHandlerContext ctx) {
        if (req.isSuccess()) {
            String path = req.path();
            ServiceInvoker invoker = this.serviceCtx.get(path);
            if (invoker == null) {
                sendResponse(ctx, Service.INVALID_PARAMS_RESP);
                logger.warn("service path not found : {}", path);
            } else {
                doService(ctx, invoker);
            }
        } else {
            sendBadRequestResponse(ctx);
        }
    }

    protected void doService(ChannelHandlerContext ctx, ServiceInvoker invoker) {
        try {
            sendResponse(ctx, invoker.invoke(req));
        } catch (Throwable ex) {
            sendResponse(ctx, this.serviceCtx.errorHandler().handle(req, ex));
        }
    }

   protected void sendResponse(ChannelHandlerContext ctx, Response resp) {
	   sendContentResponse(ctx, HttpResponseStatus.OK, resp);
   }

   protected void sendNotFoudResponse(ChannelHandlerContext ctx) {
       sendContentResponse(ctx, HttpResponseStatus.NOT_FOUND, Response.NOT_FOUND.retain());
   }

   protected void sendBadRequestResponse(ChannelHandlerContext ctx) {
       sendContentResponse(ctx, HttpResponseStatus.BAD_REQUEST, Response.BAD_REQUEST.retain());
   }

   protected void sendContentResponse(ChannelHandlerContext ctx, HttpResponseStatus status, Response resp) {
       // Build the response object.
       FullHttpResponse response = new DefaultFullHttpResponse(
               HttpVersion.HTTP_1_1, 
               status,
               resp.content);

       setContentType(response, resp.type.val);
       setContentHeaders(response, resp);

       // Write the response.
       ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
   }

   protected void setContentHeaders(FullHttpResponse response, Response resp) {
       response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, resp.content.readableBytes());
       for(Map.Entry<String, String> header : resp.headers.entrySet()) {
           response.headers().set(header.getKey(), header.getValue());
       }
   }

    protected void setContentType(HttpResponse response, String contentType) {
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaders.Names.CONTENT_ENCODING, "UTF-8");
    }

   protected void send100Continue(ChannelHandlerContext ctx) {
       FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
       ctx.write(response);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
       ctx.close();
   }
   
}
