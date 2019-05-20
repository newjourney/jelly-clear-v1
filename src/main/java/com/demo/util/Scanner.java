package com.demo.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * class文件扫描
 */
public class Scanner {

    private static final String MANFEST_CLASS_PATH = "Class-Path";

    private static final String WIN_FILE_SEPARATOR = "\\";
    // 文件分隔符"\"
    private static final String FILE_SEPARATOR = "/";
    // package扩展名分隔符
    private static final String PACKAGE_SEPARATOR = ".";
    // java类文件的扩展名
    private static final String CLASS_FILE_EXT = ".class";
    // jar类文件的扩展名
    private static final String JAR_FILE_EXT = ".jar";

    /**
     * 获取项目的所有classpath ，包括 APP_CLASS_PATH 和所有的jar文件
     */
    private static Set<String> getClassPathes() throws Exception {
        Set<String> set = new LinkedHashSet<String>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        while (set.isEmpty() && loader != null) {
            if (loader instanceof URLClassLoader) {
                Arrays.stream(((URLClassLoader) loader).getURLs()).map(url -> url.getFile()).forEach(set::add);
            }
            loader = loader.getParent();
        }

        for (String cp : set.stream().filter(Scanner::isJarFile).collect(Collectors.toList())) {
            JarFile jarFile = new JarFile(newFile(cp));
            String manfest = (String) jarFile.getManifest().getMainAttributes().getValue(MANFEST_CLASS_PATH);
            if (!Strings.isEmpty(manfest)) {
                for (String c : manfest.split("\\s+")) {
                    if (c.contains(":"))
                        set.add(new URL(c).getFile());
                }
            }
            jarFile.close();
        }
        return set;
    }

    private static boolean isClassFile(String name) {
        return name.endsWith(CLASS_FILE_EXT);
    }

    private static boolean isJarFile(String path) {
        return path.endsWith(JAR_FILE_EXT);
    }

    /**
     * 获取文件下的所有文件(递归)
     */
    private static Set<File> getFiles(File file) {
        Set<File> files = new LinkedHashSet<File>();
        if (!file.isDirectory()) {
            files.add(file);
        } else {
            File[] subFiles = file.listFiles();
            for (File f : subFiles) {
                files.addAll(getFiles(f));
            }
        }
        return files;
    }

    /**
     * 获取文件下的所有.class文件
     */
    private static Set<File> getClassFiles(File file) {
        // 获取所有文件
        Set<File> files = getFiles(file);
        Set<File> classes = new LinkedHashSet<File>();
        // 只保留.class 文件
        for (File f : files) {
            if (isClassFile(f.getName())) {
                classes.add(f);
            }
        }
        return classes;
    }

    /**
     * 得到文件夹下所有class的全包名
     */
    private static Set<ClassEntry> getFromDir(File file) {
        Set<File> files = getClassFiles(file);
        Set<ClassEntry> classes = new LinkedHashSet<ClassEntry>();
        for (File f : files) {
            classes.add(new ClassEntry(file, f));
        }
        return classes;
    }

    /**
     * 获取jar文件里的所有class文件名
     */
    private static Set<ClassEntry> getFromJar(File file) throws Exception {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        Set<ClassEntry> classes = new LinkedHashSet<ClassEntry>();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            if (isClassFile(entry.getName())) {
                classes.add(new ClassEntry(entry));
            }
        }
        jarFile.close();
        return classes;
    }

    public static List<String> getClassPathes(String includes, String excludes) {
        return getClassPathes(new ScanMatcher(includes), new ScanMatcher(excludes));
    }

    public static List<String> getClassPathes(ScanMatcher includes, ScanMatcher excludes) {
        List<String> ret = new ArrayList<>();
        try {
            Set<String> classPathes = getClassPathes();
            for (String path : classPathes) {
                if (!isJarFile(path) || (includes.match(path) && !excludes.match(path)))
                    ret.add(path);
            }
        } catch (Exception ex) {
            // ignore
        }
        return ret;
    }

    public static List<ClassEntry> scan(String includes, String excludes) {
        return scan(new ScanMatcher(includes), new ScanMatcher(excludes));
    }

    public static List<ClassEntry> scan(ScanMatcher includes, ScanMatcher excludes) {
        List<ClassEntry> ret = new ArrayList<ClassEntry>();
        try {
            for (String path : getClassPathes()) {
                for (ClassEntry clazz : getFromPath(path)) {
                    if (includes.match(clazz.name) && !excludes.match(clazz.name)) {
                        ret.add(clazz);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            // ignore
        }
        return ret;
    }

    private static Set<ClassEntry> getFromPath(String path) throws Exception {
        return isJarFile(path) ? getFromJar(newFile(path)) : getFromDir(newFile(path));
    }

    private static File newFile(String path) {
        try {
            return new File(URLDecoder.decode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return new File(path);
        }
    }

    public static class ClassEntry {
        public final String name;
        public final long size;
        public final long modifyTime;

        public ClassEntry(File root, File file) {
            String fileName = root.toPath().relativize(file.toPath()).toString().replace(WIN_FILE_SEPARATOR,
                    FILE_SEPARATOR);
            this.name = fileName.substring(0, fileName.indexOf(CLASS_FILE_EXT)).replace(FILE_SEPARATOR,
                    PACKAGE_SEPARATOR);
            this.size = file.length();
            this.modifyTime = file.lastModified();
        }

        public ClassEntry(JarEntry entry) {
            this.name = entry.getName().substring(0, entry.getName().indexOf(CLASS_FILE_EXT)).replace(FILE_SEPARATOR,
                    PACKAGE_SEPARATOR);
            this.size = entry.getSize();
            this.modifyTime = entry.getTime();
        }

        public ClassEntry(String name, long size, long modifyTime) {
            this.name = name;
            this.size = size;
            this.modifyTime = modifyTime;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class ScanMatcher {
        Pattern[] patterns;

        public ScanMatcher(String res) {
            if (res == null || res.length() == 0) {
                patterns = new Pattern[0];
                return;
            }

            String[] regs = res.split(";");
            patterns = new Pattern[regs.length];
            for (int i = 0; i < regs.length; i++) {
                String reg = regs[i];
                reg = reg.endsWith("*") ? reg : reg + "$";
                reg = reg.replace(".", "\\.");
                reg = reg.replace("*", ".*");
                patterns[i] = Pattern.compile(reg);
            }
        }

        public boolean match(String path) {
            for (Pattern p : patterns) {
                if (p.matcher(path).find()) {
                    return true;
                }
            }
            return false;
        }
    }

}
