package com.demo.http.decode;

import java.net.URLDecoder;

import com.demo.util.Strings;

public class HttpURI extends QueryString {

    private String uri;
    private String path;
    
    public HttpURI(String uri) {
        super(uri, true);
        this.uri = uri;
    }
    
    /**
     * Returns the uri used to initialize this {@link URLDecoder}.
     */
    public String uri() {
        return uri;
    }

    /**
     * Returns the decoded path string of the URI.
     */
    public String path() {
        if (path == null) {
            path = Strings.trim(originPath(), '/');
        }
        return path;
    }

    public String originPath() {
        int pathEndPos = uri.indexOf('?');
        return decodeComponent(pathEndPos < 0 ? uri : uri.substring(0, pathEndPos), charset);
    }
    
    public String purePath() {
        return Strings.trim(path(), '/');
    }

}
