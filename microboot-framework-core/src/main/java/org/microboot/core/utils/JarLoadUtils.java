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
 *
 * 这个工具类中有两个内部类，分别是InnerURLClassLoaderManager和InnerURLClassLoader
 * InnerURLClassLoaderManager通过ConcurrentHashMap保存InnerURLClassLoader实例
 * InnerURLClassLoader是真正用来加载jar包和class的加载器
 * 步骤1：加载jar包
 * 步骤2：加载指定jar包下的class类
 *
 * 方法名带ByCustom后缀加载的jar包可以卸载
 * 方法名不带ByCustom的暂时没有提供卸载jar包的功能
 * 【
 * 测试过很多方案都不行，参考：https://www.cnblogs.com/diyunpeng/p/2391291.html
 * 参考案例关注一下URLClassPath的内部类JarLoader的csu属性，它里面存了加载的jar包路径
 * 这个方案看上去合理，但实验后还是不行，只是作为一个学习参考记录在这吧
 * 】
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

            InnerURLClassLoader innerURLClassLoader = new InnerURLClassLoader();

            /*
                执行到此处，说明LOADER_CACHE中还没有jar对应的InnerURLClassLoader对象
                但有可能是多线程同时只能到此处，因此会存在并发问题
                computeIfAbsent是线程安全的，在多线程下只有一个线程能成功将自己的innerURLClassLoader对象存入LOADER_CACHE并返回
             */
            urlClassLoader = LOADER_CACHE.computeIfAbsent(jar, j -> innerURLClassLoader);

            //这里的判断是防止多线程场景下，重复加载jar包
            if (urlClassLoader != innerURLClassLoader) {
                return;
            }

            /*
                加载jar包的url固定写法：jar:file:/....!/
                由于Linux系统的绝对路径是以“/”开头，所以会多一个“/”，导致URL读取不到JAR包
            */
            String newJar = PathUtils.regular(jar, true);
            URL jarUrl = new URL("jar:file:/" + newJar + "!/");

            //一个InnerURLClassLoader对象就对应一个Jar包
            urlClassLoader.loadJar(jarUrl);
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