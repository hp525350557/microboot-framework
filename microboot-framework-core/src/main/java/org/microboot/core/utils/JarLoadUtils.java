package org.microboot.core.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.func.FuncV1;

import java.io.File;
import java.lang.reflect.Method;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 胡鹏
 */
public class JarLoadUtils {

    private static final JarLoadUtils instance = new JarLoadUtils();

    private final InnerURLClassLoaderManager manager = new InnerURLClassLoaderManager();

    private JarLoadUtils() {
    }

    public static JarLoadUtils getInstance() {
        return instance;
    }

    public void load(String path, FuncV1<String> func) throws Exception {
        if (StringUtils.isBlank(path)) {
            return;
        }
        File jarFile = new File(path);
        if (!jarFile.exists()) {
            return;
        }
        if (jarFile.isFile()) {
            if (!StringUtils.endsWithIgnoreCase(path, ".jar")) {
                return;
            }
            func.func(path);
        } else {
            File[] files = jarFile.listFiles();
            if (ArrayUtils.isEmpty(files)) {
                return;
            }
            for (File file : files) {
                this.load(file.getAbsolutePath(), func);
            }
        }
    }

    public void loadJarByCustom(String jar) throws Exception {
        this.manager.loadJar(jar);
    }

    public void unloadJarByCustom(String jar) {
        this.manager.unloadJar(jar);
    }

    @SuppressWarnings("rawtypes")
    public Class loadClassByCustom(String jar, String className) throws Exception {
        return this.manager.loadClass(jar, className);
    }

    public void loadJar(String jar) throws Exception {
        URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        Class<?> loaderClass = URLClassLoader.class;
        //addURL是URLClassLoader的方法名
        Method method = loaderClass.getDeclaredMethod("addURL", URL.class);
        //setAccessible是启用和禁用访问安全检查的开关，提高反射性能
        //true：则指示反射的对象在使用时应该取消 Java 语言访问检查。
        //false：则指示反射的对象应该实施 Java 语言访问检查。
        //由于JDK的安全检查耗时较多.所以通过setAccessible(true)的方式关闭安全检查就可以达到提升反射速度的目的
        method.setAccessible(true);
        //由于Linux系统的绝对路径是以“/”开头，所以会多一个“/”，导致URL读取不到JAR包
        String newJar = PathUtils.regular(jar, true);
        URL url = new URL("file:/" + newJar);
        //加载Jar包
        method.invoke(urlClassLoader, url);
    }

    private final class InnerURLClassLoaderManager {

        private final ConcurrentHashMap<String, InnerURLClassLoader> LOADER_CACHE = new ConcurrentHashMap<>();

        private InnerURLClassLoaderManager() {
        }

        private void loadJar(String jar) throws MalformedURLException {
            InnerURLClassLoader urlClassLoader = LOADER_CACHE.get(jar);
            if (urlClassLoader != null) {
                return;
            }
            //注意，一个InnerURLClassLoader对象就对应一个Jar包
            urlClassLoader = new InnerURLClassLoader();
            /*
                加载jar包的url固定写法：jar:file:/....!/
                由于Linux系统的绝对路径是以“/”开头，所以会多一个“/”，导致URL读取不到JAR包
            */
            String newJar = PathUtils.regular(jar, true);
            URL jarUrl = new URL("jar:file:/" + newJar + "!/");
            urlClassLoader.loadJar(jarUrl);
            LOADER_CACHE.put(jar, urlClassLoader);
        }

        private void unloadJar(String jar) {
            InnerURLClassLoader urlClassLoader = LOADER_CACHE.get(jar);
            if (urlClassLoader == null) {
                return;
            }
            urlClassLoader.unloadJar();
            LOADER_CACHE.remove(jar);
        }

        @SuppressWarnings("rawtypes")
        private Class loadClass(String jar, String className) throws ClassNotFoundException {
            InnerURLClassLoader urlClassLoader = LOADER_CACHE.get(jar);
            if (urlClassLoader == null) {
                return null;
            }
            return urlClassLoader.loadClass(className);
        }
    }

    private final class InnerURLClassLoader extends URLClassLoader {

        private final Logger logger = LogManager.getLogger(this.getClass());

        private JarURLConnection jarURLConnection = null;

        private InnerURLClassLoader() {
            super(new URL[]{}, Thread.currentThread().getContextClassLoader());
        }

        /**
         * 加载Jar包
         *
         * @param url
         */
        private void loadJar(URL url) {
            try {
                //打开文件url连接
                URLConnection urlConnection = url.openConnection();
                if (urlConnection instanceof JarURLConnection) {
                    urlConnection.setUseCaches(true);
                    ((JarURLConnection) urlConnection).getManifest();
                    this.jarURLConnection = (JarURLConnection) urlConnection;
                }
                //将指定的URL添加到URL列表中，以便搜索类和资源。
                this.addURL(url);
            } catch (Exception e) {
                LoggerUtils.error(logger, e);
            }
        }

        /**
         * 卸载Jar包
         */
        private void unloadJar() {
            if (this.jarURLConnection == null) {
                return;
            }
            try {
                this.jarURLConnection.getJarFile().close();
                this.jarURLConnection = null;
            } catch (Exception e) {
                LoggerUtils.error(logger, e);
            }
        }
    }
}