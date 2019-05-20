package com.demo.http.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.demo.http.decode.HttpBody;
import com.demo.http.decode.HttpURI;
import com.demo.http.decode.IParameters;
import com.demo.http.decode.QueryString;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * 
 * http request
 * 
 * @author xingkai.zhang
 *
 */
public class Request implements IParameters {
    
    private InetAddress address;
    private HttpRequest request;
    private HttpHeaders headers;
    private HttpMethod method;
    
    private HttpBody body;
    private HttpURI uri;
    
    private List<Throwable> causes;
   
    public Request(InetAddress address, HttpRequest request) {
        this(address, request.getUri(), request.headers(), request.getMethod());
        this.request = request;
    }
    public Request(InetAddress address, String uri, HttpHeaders headers, HttpMethod method) {
        this.address = address;
        this.uri = new HttpURI(uri);
        this.headers = headers;
        this.method = method;
    }

    public List<Throwable> causes() {
        return causes;
    }
    
    public byte[] content() {
        return this.body == null ? null : this.body.asBytes();
    }
    
    public HttpBody body() {
        return this.body;
    }

    public void appendDecoderResult(DecoderResult dr) {
        if(dr.isSuccess()) {
            return;
        }
        if(causes == null) {
            causes = new ArrayList<Throwable>();
        }
        causes.add(dr.cause());
    }

    public String remoteHost() {
        return this.address.getHostAddress();
    }
    
    public InetAddress address() {
        return this.address;
    }
    
    public String getHeader(String name) {
        return this.headers.get(name);
    }
    
    public HttpHeaders headers() {
        return this.headers;
    }
    
    public HttpMethod method() {
        return this.method;
    }
    
    public String uri() {
        return uri.uri();
    }
    
    public String queryString() {
        return uri.queryString();
    }
    
    public String originPath() {//真实路径
       return uri.originPath();
    }
    
    /**
     * Returns the decoded path string of the URI. without (prefix and suffix '/')
     */
    public String path() {
        return uri.path();
    }
    
    public Set<String> paramNames() {
        return uri.parameters().keySet();
    }
    @Override
    public Set<String> getParamNames() {
        return uri.parameters().keySet();
    }
    public List<String> getParamValues(String name) {
        return uri.parameters().get(name);
    }

    public void appendContent(HttpContent content) {
        if(content.getDecoderResult().isSuccess()) {
            if(this.body == null) {
                this.body = new HttpBody(request);
            }
            this.body.offer(content);
        } else {
            this.appendDecoderResult(content.getDecoderResult());
        }
    }

    public boolean isSuccess() {
        return this.causes == null;
    }
    
    public static class Params extends QueryString implements IParameters {
        public Params(String uri) {
            super(uri, false);
        }
        public Set<String> keySet() {
            return parameters().keySet();
        }
        public List<String> getVals(String name) {
            return parameters().get(name);
        }
        public String get(String name) {
            return get(name, 0);
        }
        public String get0(String name) {
            return get(name, 0);
        }
        public String get(String name, int i) {
            List<String> vals = parameters().get(name);
            return (vals != null && vals.size() > i) ? vals.get(i) : null;
        }
        public static Params decode(String queryString) {
            return new Params(queryString);
        }
        @Override
        public Set<String> getParamNames() {
            return parameters().keySet();
        }
        @Override
        public List<String> getParamValues(String name) {
            return parameters().get(name);
        }
    }

}
