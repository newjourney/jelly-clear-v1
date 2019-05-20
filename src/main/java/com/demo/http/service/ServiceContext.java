package com.demo.http.service;

import java.lang.reflect.Modifier;
import java.util.List;

import com.demo.http.service.uri.PathMap;
import com.demo.http.service.uri.PathMatcher;
import com.demo.http.service.uri.PathPattern;
import com.demo.http.service.uri.PathTemplate;
import com.demo.util.Log;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class ServiceContext {

    private PathMap<Pair> services;
    private ServiceErrorHandler errorHandler;

    private ServiceContext() {
        services = new PathMap<>();
        setErrorHandler(v -> errorHandler = v);
    }

    private static final class Holder {
        private static final ServiceContext INSTANCE = new ServiceContext();
    }

    public static ServiceContext instance() {
        return Holder.INSTANCE;
    }
    
    public void init() {
        List<Class<?>> classes = Codes.getClasses("jelly-clear-*-SNAPSHOT.jar;com.demo.*", "");
        registServices(classes);
    }

    public void registService(String path, Service service) {
        PathTemplate temp = new PathTemplate(path);
        PathPattern pattern = new PathPattern(temp);
        Pair old = services.put(temp.mapping(), new Pair(pattern, service));
        if (old != null) {
            Log.warn("Duplicate service path : {}", temp.mapping());
        }
    }

    public static class ServiceInvoker {
        final PathMatcher matcher;
        final Service service;

        ServiceInvoker(PathMatcher matcher, Service service) {
            this.matcher = matcher;
            this.service = service;
        }

        public Response invoke(Request req) {
            return service.service(req, matcher);
        }
    }

    public static class Pair {
        PathPattern pattern;
        Service serivce;

        Pair(PathPattern pattern, Service service) {
            this.pattern = pattern;
            this.serivce = service;
        }
    }

    public ServiceInvoker get(String path) {
        Pair pair = services.get(path);
        if (pair != null) {
            PathMatcher matcher = pair.pattern.compile(path);
            if (matcher.find()) {
                return new ServiceInvoker(matcher, pair.serivce);
            }
        }
        return null;
    }

    public int size() {
        return services.size();
    }

    private void registServices(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            registService(clazz);
        }
    }

    public void registService(Class<?> clazz) {
        Http ann = clazz.getAnnotation(Http.class);
        if (ann != null && !Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isInterface(clazz.getModifiers())) {
            registService(ann.value(), (Service) newInstance(clazz));
        }
    }

    private Service newInstance(Class<?> clazz) {
        try {
            return (Service) clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(clazz.getName(), e);
        }
    }

    public ServiceErrorHandler errorHandler() {
        return this.errorHandler;
    }

    public void setErrorHandler(ErrorHandlerSetter setter) {
        setter.set((r, e) -> Service.INVALID_PARAMS_RESP);
    }

    public interface ErrorHandlerSetter {
        void set(ServiceErrorHandler errorHandler);
    }

}
