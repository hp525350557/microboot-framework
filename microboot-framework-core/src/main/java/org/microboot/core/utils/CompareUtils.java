package org.microboot.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author 胡鹏
 */
public class CompareUtils {

    /**
     * 大于
     * 当isEq = true时，则比较大于等于
     *
     * @param value
     * @param valueRange
     * @param isEq
     * @return
     */
    public static boolean greateThan(String value, String valueRange, boolean isEq) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(valueRange)) {
            return false;
        }
        boolean bl = NumberUtils.isCreatable(valueRange);
        if (bl) {
            if (!NumberUtils.isCreatable(value)) {
                throw new IllegalArgumentException("The parameter [" + value + "] should be numeric");
            }
            double valueD = NumberUtils.toDouble(value);
            double valueRangeD = NumberUtils.toDouble(valueRange);
            if (isEq) {
                return valueD >= valueRangeD;
            } else {
                return valueD > valueRangeD;
            }
        } else {
            int compare = StringUtils.compare(value, valueRange);
            if (isEq) {
                return compare >= 0;
            } else {
                return compare > 0;
            }
        }
    }

    /**
     * 小于
     * 当isEq = true时，则比较小于等于
     *
     * @param value
     * @param valueRange
     * @param isEq
     * @return
     */
    public static boolean lessThan(String value, String valueRange, boolean isEq) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(valueRange)) {
            return false;
        }
        boolean bl = NumberUtils.isCreatable(valueRange);
        if (bl) {
            if (!NumberUtils.isCreatable(value)) {
                throw new IllegalArgumentException("The parameter [" + value + "] should be numeric");
            }
            double valueD = NumberUtils.toDouble(value);
            double valueRangeD = NumberUtils.toDouble(valueRange);
            if (isEq) {
                return valueD <= valueRangeD;
            } else {
                return valueD < valueRangeD;
            }
        } else {
            int compare = StringUtils.compare(value, valueRange);
            if (isEq) {
                return compare <= 0;
            } else {
                return compare < 0;
            }
        }
    }

    /**
     * 区间
     *
     * @param value
     * @param minRange
     * @param maxRange
     * @param leftOpen
     * @param rightOpen
     * @return
     */
    public static boolean between(String value, String minRange, String maxRange, boolean leftOpen, boolean rightOpen) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(minRange) || StringUtils.isBlank(maxRange)) {
            return false;
        }
        boolean bl = NumberUtils.isCreatable(minRange) && NumberUtils.isCreatable(maxRange);
        if (bl) {
            if (!NumberUtils.isCreatable(value)) {
                throw new IllegalArgumentException("The parameter [" + value + "] should be numeric");
            }
            double valueD = NumberUtils.toDouble(value);
            double minRangeD = NumberUtils.toDouble(minRange);
            double maxRangeD = NumberUtils.toDouble(maxRange);
            if (leftOpen && rightOpen) {
                //左开右开
                return valueD > minRangeD && valueD < maxRangeD;
            } else if (!leftOpen && rightOpen) {
                //左闭右开
                return valueD >= minRangeD && valueD < maxRangeD;
            } else if (leftOpen && !rightOpen) {
                //左开右闭
                return valueD > minRangeD && valueD <= maxRangeD;
            } else if (!leftOpen && !rightOpen) {
                //左闭右闭
                return valueD >= minRangeD && valueD <= maxRangeD;
            }
        } else {
            int minCompare = StringUtils.compare(value, minRange);
            int maxCompare = StringUtils.compare(value, maxRange);
            if (leftOpen && rightOpen) {
                //左开右开
                return minCompare > 0 && maxCompare < 0;
            } else if (!leftOpen && rightOpen) {
                //左闭右开
                return minCompare >= 0 && maxCompare < 0;
            } else if (leftOpen && !rightOpen) {
                //左开右闭
                return minCompare > 0 && maxCompare <= 0;
            } else if (!leftOpen && !rightOpen) {
                //左闭右闭
                return minCompare >= 0 && maxCompare <= 0;
            }
        }
        return false;
    }
}
