/**
 * Copyright (C), 2011-2017
 * File Name: DateUtils.java
 * Encoding: UTF-8
 * Date: 17-9-5 下午6:32
 * History:
 */
package com.mars.kit.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @FileName  DateUtils.java
 * @author colley
 * @version 1.0  Date: 17-9-5 下午6:32
 */
public class DateTimeUtils {
    private static Log logger = LogFactory.getLog(DateTimeUtils.class);

    public static Calendar string2Calendar(String strtime) {
        Date d = string2Date(strtime);

        if (d != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(d);

            return c;
        }

        return null;
    }

    public static Date string2Date(String strtime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DFormatEnum.DATA_FORMAT_DEFAULT);

            return dateFormat.parse(strtime, new ParsePosition(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date string2Date(String strtime, String datepattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(datepattern);

            return dateFormat.parse(strtime, new ParsePosition(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Timestamp string2Timestamp(String strtime, String datepattern) {
        Date date = string2Date(strtime, datepattern);

        if (date != null) {
            return new Timestamp(date.getTime());
        }

        return null;
    }

    public static Timestamp string2Timestamp(String strtime, DFormatEnum datepattern) {
        Date date = string2Date(strtime, datepattern.pattern);

        if (date != null) {
            return new Timestamp(date.getTime());
        }

        return null;
    }

    public static String calendar2String(Calendar time) {
        return date2String(time.getTime());
    }

    public static String date2String(Date time, String datepattern) {
        if (StringUtils.isEmpty(datepattern)) {
            datepattern = DFormatEnum.DATA_FORMAT_DEFAULT;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(datepattern);

        try {
            return dateFormat.format(time);
        } catch (Exception e) {
            logger.error(e);

            return null;
        }
    }

    public static String date2String(Timestamp time, String datepattern) {
        if (StringUtils.isEmpty(datepattern)) {
            datepattern = DFormatEnum.DATA_FORMAT_DEFAULT;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(datepattern);

        try {
            return dateFormat.format(time);
        } catch (Exception e) {
            logger.error(e);

            return null;
        }
    }

    public static String date2String(Date time) {
        return date2String(time, null);
    }

    public static String date2String(Timestamp time) {
        return date2String(time, null);
    }

    public static String getSystemTime(String format) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(format);

        return df.format(date);
    }

    /**
     * 将String类型日期转换为Date类型的日期
     *
     * @param strDate
     * @param formatter
     * @return
     */
    public static Date parseDateByStr(String strDate, String formatter) {
        if ((formatter == null) || formatter.trim().equals("")) {
            formatter = DFormatEnum.YYYY_MM_DD.pattern;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter);

        // 必须捕获异常
        try {
            Date date = simpleDateFormat.parse(strDate);

            return date;
        } catch (ParseException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public static String formateDateShort(Date date) {
        return date2String(date, DFormatEnum.YYYY_MM_DD.pattern);
    }

    public static String getStringDateShort() {
        return date2String(new Date(), DFormatEnum.YYYY_MM_DD.pattern);
    }

    public static String getStringDateForCahce() {
        return date2String(new Date(), DFormatEnum.YYYYMMDD.pattern);
    }

    public static String formateDateFull(Date date) {
        return date2String(date, DFormatEnum.YYYY_MM_DDHH_MM_SS.pattern);
    }

    public static String formateDateFull() {
        return formateDateFull(new Date());
    }

    public static Date fomateStringDate(String str) {
        try {
            int index = str.indexOf(":");
            SimpleDateFormat df = null;

            if (index != -1) {
                df = new SimpleDateFormat(DFormatEnum.YYYY_MM_DDHH_MM_SS.pattern);
            } else {
                df = new SimpleDateFormat(DFormatEnum.YYYY_MM_DD.pattern);
            }

            Date date = df.parse(str);

            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date getLastTimeDay(Date date) {
        String dateTemp = formateDateShort(date);
        Date date1 = fomateStringDate(dateTemp + " 23:59:59");

        return date1;
    }

    /**
     * 获取在给定日期的基础上往前/后的第几周
     *
     * @param date 给定日期
     * @param gap 移动的周数
     * @return 年+周 例如 201532,2015年的第32周
     */
    public static int weekOfYear(Date date, Integer gap) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, gap);

        return Integer.valueOf(String.valueOf(calendar.get(Calendar.YEAR)) + String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
    }

    /**
     * 获取当前系统时间
     *
     * @return
     */
    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }
}
