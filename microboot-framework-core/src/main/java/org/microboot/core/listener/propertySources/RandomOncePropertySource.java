package org.microboot.core.listener.propertySources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.microboot.core.utils.UUIDUtils;
import org.springframework.core.env.PropertySource;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 胡鹏
 */
public class RandomOncePropertySource extends PropertySource<ThreadLocalRandom> {

    private static final Log logger = LogFactory.getLog(RandomOncePropertySource.class);

    /**
     * Name of the random {@link PropertySource}.
     */
    private static final String RANDOM_PROPERTY_SOURCE_NAME = "randomOnce";

    private static final String PREFIX = "randomOnce.";

    //自定义属性-int
    private static int attrToInt = -1;

    //自定义属性-long
    private static long attrToLong = -1;

    //自定义属性-String
    private static String attrToString;

    //自定义属性-Object
    private static Object attrToObject;

    private RandomOncePropertySource(String name) {
        // ThreadLocalRandom比Random性能更高
        // java7在所有情形下都更推荐使用ThreadLocalRandom，它向下兼容已有的代码且运营成本更低
        super(name, ThreadLocalRandom.current());
    }

    public RandomOncePropertySource() {
        this(RANDOM_PROPERTY_SOURCE_NAME);
    }

    @Override
    public Object getProperty(String name) {
        if (!name.startsWith(PREFIX)) {
            return null;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Generating random property for '" + name + "'");
        }
        return getRandomValue(name.substring(PREFIX.length()));
    }

    private Object getRandomValue(String type) {
        //匹配${randomOnce.int}
        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase("int", type)) {
            if (attrToInt < 0) {
                attrToInt = getSource().nextInt();
            }
            return attrToInt;
        }
        //匹配${randomOnce.long}
        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase("long", type)) {
            if (attrToLong < 0) {
                attrToLong = getSource().nextLong();
            }
            return attrToLong;
        }
        //匹配${randomOnce.int[0,65535]}
        //此处type = "int[0,65535]"
        String range = getRange(type, "int");
        if (range != null) {
            if (attrToInt < 0) {
                attrToInt = getNextIntInRange(range);
            }
            return attrToInt;
        }
        //匹配${randomOnce.long[0,65535]}
        //此处type = "long[0,65535]"
        range = getRange(type, "long");
        if (range != null) {
            if (attrToLong < 0) {
                attrToLong = getNextLongInRange(range);
            }
            return attrToLong;
        }
        //匹配${randomOnce.uuid}
        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase("uuid", type)) {
            if (attrToString == null) {
                attrToString = UUIDUtils.uuidFor32();
            }
            return attrToString;
        }
        //默认值
        if (attrToObject == null) {
            attrToObject = getRandomBytes();
        }
        return attrToObject;
    }

    private String getRange(String type, String prefix) {
        if (type.startsWith(prefix)) {
            int startIndex = prefix.length() + 1;
            if (type.length() > startIndex) {
                return type.substring(startIndex, type.length() - 1);
            }
        }
        return null;
    }

    private int getNextIntInRange(String range) {
        String[] ranges = StringUtils.commaDelimitedListToStringArray(range);
        if (ranges == null || ranges.length < 2) {
            return Math.abs(getSource().nextInt());
        }
        int start = Integer.parseInt(ranges[0]);
        int end = Integer.parseInt(ranges[1]);
        int difference = end - start;
        return start + Math.abs(getSource().nextInt()) % difference;
    }

    private long getNextLongInRange(String range) {
        String[] ranges = StringUtils.commaDelimitedListToStringArray(range);
        if (ranges == null || ranges.length < 2) {
            return Math.abs(getSource().nextLong());
        }
        long start = Long.parseLong(ranges[0]);
        long end = Long.parseLong(ranges[1]);
        long difference = end - start;
        return start + Math.abs(getSource().nextLong()) % difference;
    }

    private Object getRandomBytes() {
        byte[] bytes = new byte[32];
        getSource().nextBytes(bytes);
        return DigestUtils.md5DigestAsHex(bytes);
    }
}
