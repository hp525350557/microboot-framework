package org.microboot.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author 胡鹏
 */
public class PropertiesUtils {

    private static final Logger logger = LogManager.getLogger(PropertiesUtils.class);

    private static final PropertiesUtils instance = new PropertiesUtils();

    private PropertiesUtils() {
    }

    public static PropertiesUtils getInstance() {
        return instance;
    }

    public Properties getProperties(String path) {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            properties.load(in);
        } catch (Throwable e) {
            in = this.getClass().getClassLoader().getResourceAsStream(path);
            try {
                properties.load(in);
            } catch (Throwable ex) {
                LoggerUtils.error(logger, ex);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }
}
