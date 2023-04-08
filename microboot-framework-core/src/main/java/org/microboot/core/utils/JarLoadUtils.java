package org.microboot.core.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.func.FuncV1;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
 *
 * 老版本的InnerURLClassLoader中，加载和卸载Jar如下：
 *
 * private JarURLConnection jarURLConnection = null;
 *
 * private void loadJar(URL url) {
 *     try {
 *         //打开文件url连接
 *         URLConnection urlConnection = url.openConnection();
 *         if (urlConnection instanceof JarURLConnection) {
 *             urlConnection.setUseCaches(true);
 *             ((JarURLConnection) urlConnection).getManifest();
 *             this.jarURLConnection = (JarURLConnection) urlConnection;
 *         }
 *         //将指定的URL添加到URL列表中，以便搜索类和资源。
 *         this.addURL(url);
 *     } catch (Exception e) {
 *         LoggerUtils.error(logger, e);
 *     }
 * }
 *
 * private void unloadJar() {
 *     if (this.jarURLConnection == null) {
 *         return;
 *     }
 *     try {
 *         this.jarURLConnection.getJarFile().close();
 *         this.jarURLConnection = null;
 *     } catch (Exception e) {
 *         LoggerUtils.error(logger, e);
 *     }
 * }
 *
 * 加载Jar包时：通过URL对象获取Jar包的连接并保存，但加载Jar包的核心是this.addURL(url)
 * 卸载Jar包时：关闭连接
 * 问：为什么要获取并保存JarURLConnection对象？
 * 答：因为在Windows上如果某个文件被访问则无法删除。
 * 在microboot定时任务模块中，有一个功能需要热更新外部插件Jar包中的class
 * 如果无法删除Jar包，则无法实现热更新
 *
 * 但从JDK1.7开始，URLClassLoader就提供了close()方法用来释放ClassLoader的资源，因此不需要上面那么复杂
 *
 * 扩展：
 *     JarLoadUtils并不是真正的热更新，只是利用了JVM中不同ClassLoader对象可以加载同一个class文件的特点【因为ClassLoader的缓存和ClassLoader的对象绑定】
 *     由于在unloadJar方法中删除了老的InnerURLClassLoader对象，当再次调用loadJar时会创建一个新的InnerURLClassLoader对象
 *     当使用新的InnerURLClassLoader对象再次加载Class时，导致JVM方法区中同一个Class文件会有两个不同的Class对象
 *     但因为老的InnerURLClassLoader对象已经从Map中移除，没有引用指向它，当执行GC之后就会回收，连带它加载的Class也会被回收【前提是老的Class没有存活的实例对象了】
 *     因此InnerURLClassLoader加载的Class并不是在断开Jar包连接或者InnerURLClassLoader被close时被删除的
 *     所以JarLoadUtils的热更新和Arthas的热更新是不同的，Arthas的热更新是用了Java的Agent机制，是直接替换了JVM方法区的Class信息，所以是实时的
 *
 * 遇到问题：
 *     microboot的定时任务执行过程中，InnerURLClassLoader对象却一直不回收，每加载一次Jar包，内存中就多出一个InnerURLClassLoader对象
 * 实验结果：
 *     InnerURLClassLoader对象想要被GC回收，需要满足三个条件：
 *     1、没有引用指向InnerURLClassLoader加载的Class
 *     2、没有引用指向Class关联的对象
 *     3、没有引用指向InnerURLClassLoader对象
 * 结论推测：
 *     1、通过JarLoadUtils的逻辑可以确定，卸载Jar包之后，理论上应该没有引用指向InnerURLClassLoader对象
 *     2、通过jvisualvm工具监控以及Spring的ApplicationContext容器获取指定beanName都可以确定，Class关联的Bean对象确实被注销了，并且被GC回收了
 *        【
 *          观察jvisualvm的抽样器可检测堆的实时结果
 *          导出dump，然后通过jhat分析，通过OQL语句查询
 *          通过jmap -histo命令，查看实例数
 *        】
 *     3、通过jvisualvm工具和jhat分析，虽然JarLoadUtils中缓存InnerURLClassLoader对象的map删除了，但还是有很多引用指向它
 *        经过对比发现，这些引用在创建普通对象时是没有的，怀疑是Spring容器给加上的，但是在注销的时候又没有同步删除
 * 解决方案：
 *     不再使用Spring容器来管理外部加载的class了，直接使用一个ConcurrentHashMap来存储相关对象，经过测试，确实可行
 * 总结：
 *     使用自定义ClassLoader加载第三方jar以及class
 *     然后将class交给Spring去创建Bean以及管理Bean生命周期过程中
 *     会给ClassLoader增加一些额外的引用
 *     这些引用在Bean销毁时并没有被收回，导致ClassLoader的对象无法被GC回收
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

    public static final class InnerURLClassLoader extends URLClassLoader {

        private final Logger logger = LogManager.getLogger(this.getClass());

        private InnerURLClassLoader() {
            super(new URL[]{}, Thread.currentThread().getContextClassLoader());
        }

        /**
         * 加载Jar包
         *
         * @param url
         */
        private void loadJar(URL url) {
            //将指定的URL添加到URL列表中，以便搜索类和资源。
            this.addURL(url);
        }

        /**
         * 卸载Jar包
         */
        private void unloadJar() {
            try {
                this.close();
            } catch (Exception e) {
                LoggerUtils.error(logger, e);
            }
        }
    }
}