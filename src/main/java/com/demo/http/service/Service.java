package com.demo.http.service;

import com.demo.http.service.uri.PathMatcher;

/**
 * 
 * http service method interface
 * 
 * @author xingkai.zhang
 * 
 */
public interface Service {
    
    Response INVALID_PARAMS_RESP = new Response("INVALID PARAMS");

	public default Response service(Request req, PathMatcher matcher) {
		return service(req);
	}

	public Response service(Request req);

}
