package com.demo.repo;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo.http.service.Codes;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class RepoContext {

    private Map<Class<?>, BasicRepo> repoMap = new HashMap<>();

    private RepoContext() {
        this.init();
    }

    private static class Holder {
        private static final RepoContext INSTANCE = new RepoContext();
    }

    public static RepoContext instance() {
        return Holder.INSTANCE;
    }

    public void init() {
        List<Class<?>> classes = Codes.getClasses("jelly-clear-*-SNAPSHOT.jar;com.demo.*", "");
        registRepos(classes);
    }

    private void registRepos(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            registRepo(clazz);
        }
    }

    public void registRepo(Class<?> clazz) {
        Repo ann = clazz.getAnnotation(Repo.class);
        if (ann != null && !Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isInterface(clazz.getModifiers())) {
            repoMap.put(clazz, (BasicRepo) newInstance(clazz));
        }
    }

    private BasicRepo newInstance(Class<?> clazz) {
        try {
            return (BasicRepo) clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(clazz.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) repoMap.get(clazz);
    }
}
