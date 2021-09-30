/*
 * Copyright (c) 2015-2021, Harvey Chan. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.harveychan.canal.client.util;

import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * 字符串转换工具类
 *
 * @author canhungwai
 * @since 2021-09-14
 */
public class StringConvertUtil {

    private static final String[] PARSE_PATTERNS = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
            "yyyy.MM.dd HH:mm", "yyyy.MM"};

    private StringConvertUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 根据类型转换字段值
     *
     * @param type        字段类型
     * @param columnValue 字段值字符串
     * @return
     */
    static Object convertType(Class<?> type, String columnValue) {
        if (columnValue == null || columnValue.length() == 0) {
            return null;
        } else if (type.equals(Integer.class)) {
            return Integer.parseInt(columnValue);
        } else if (type.equals(Long.class)) {
            return Long.parseLong(columnValue);
        } else if (type.equals(Boolean.class)) {
            return convertToBoolean(columnValue);
        } else if (type.equals(BigDecimal.class)) {
            return new BigDecimal(columnValue);
        } else if (type.equals(Double.class)) {
            return Double.parseDouble(columnValue);
        } else if (type.equals(Float.class)) {
            return Float.parseFloat(columnValue);
        } else if (type.equals(Date.class)) {
            return parseDate(columnValue);
        } else {
            return type.equals(java.sql.Date.class) ? parseSqlDate(columnValue) : columnValue;
        }
    }

    /**
     * 将日期字符串转换成日期对象
     *
     * @param str
     * @return
     */
    private static Date parseDate(String str) {
        if (str == null) {
            return null;
        } else {
            try {
                return DateUtils.parseDate(str, PARSE_PATTERNS);
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    /**
     * 将日期字符串转换成日期对象
     *
     * @param str
     * @return
     */
    private static Date parseSqlDate(String str) {
        if (str == null) {
            return null;
        } else {
            try {
                Date date = DateUtils.parseDate(str, PARSE_PATTERNS);
                return new java.sql.Date(date.getTime());
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    /**
     * 将字符串转换成布尔对象
     *
     * @param value
     * @return
     */
    private static boolean convertToBoolean(String value) {
        return "1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }
}
