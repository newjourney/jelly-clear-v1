package com.demo.http.service;

/**
 * 
 * @author xingkai.zhang
 *
 */
public interface ServiceErrorHandler {

	public Response handle(Request req, Throwable e);

}
