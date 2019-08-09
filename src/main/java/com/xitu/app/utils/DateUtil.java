package com.xitu.app.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * 日期操作工具类s
 *
 */
public class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss_SSS";

    // RFC 822 Date Format
    public static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    // ISO 8601 format
    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // Alternate ISO 8601 format without fractional seconds
    public static final String ALTERNATIVE_ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String SHORT_ALTERNATIVE_ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";

    public static final String SIMPLE_ALTERNATIVE_ISO8601_DATE_FORMAT = "yyyy-MM-dd'Z'";

    /**
     * 字符串转换为日期格式
     *
     * @param strDate
     * @param pattern
     * @return
     */
    public static Date stringToDate(String strDate, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期格式转换为字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String dateToString(Date date, String pattern) {
        try {
            if (date == null) {
                return null;
            }
            return new SimpleDateFormat(pattern).format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得指定格式的当前日期
     *
     * @param pattern
     * @return
     */
    public static String getCurrentDate(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * 获得 GMT 格式的当前日期
     *
     * @return
     */
    public static String getCurrentGMT() {
        return formatRfc822Date(new Date());
    }

    /**
     * Formats Date to GMT string.
     *
     * @param date
     * @return
     */
    public static String formatRfc822Date(Date date) {
        return getRfc822DateFormat().format(date);
    }

    /**
     * Parses a GMT-format string.
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseRfc822Date(String dateString) throws ParseException {
        return getRfc822DateFormat().parse(dateString);
    }

    private static DateFormat getRfc822DateFormat() {
        SimpleDateFormat rfc822DateFormat = new SimpleDateFormat(RFC822_DATE_FORMAT, Locale.US);
        rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));

        return rfc822DateFormat;
    }

    /**
     * Parse a date string in the format of ISO 8601.
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseIso8601Date(String dateString) throws ParseException {
        try {
            return getIso8601DateFormat().parse(dateString);
        } catch (ParseException e) {
            return getAlternativeIso8601DateFormat(ALTERNATIVE_ISO8601_DATE_FORMAT).parse(dateString);
        }
    }

    /**
     * Parse a date string in the Simple format of ISO 8601. format like
     * yyyy-MM-dd'T'HH:mm'Z''
     *
     * @param dateString eg. 2012-11-07T09:24Z
     * @return
     * @throws ParseException
     */
    public static Date parseShortIso8601Date(String dateString) throws ParseException {
        try {
            return getIso8601DateFormat().parse(dateString);
        } catch (ParseException e) {
            return getAlternativeIso8601DateFormat(SHORT_ALTERNATIVE_ISO8601_DATE_FORMAT).parse(dateString);
        }
    }

    /**
     * Parse a date string in the Simple date of ISO 8601. format like
     * yyyy-MM-dd'Z'
     *
     * @param dateString eg 2012-11-07Z
     * @return
     * @throws ParseException
     */
    public static Date parseSimpleIso8601Date(String dateString) throws ParseException {
        try {
            return getIso8601DateFormat().parse(dateString);
        } catch (ParseException e) {
            return getAlternativeIso8601DateFormat(SIMPLE_ALTERNATIVE_ISO8601_DATE_FORMAT).parse(dateString);
        }
    }

    private static DateFormat getIso8601DateFormat() {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));

        return df;
    }

    private static DateFormat getAlternativeIso8601DateFormat(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));

        return df;
    }

    /**
     * 获取到期日与当前日期的天数间隔
     *
     * @param endDate
     * @return
     */
    public static long getDays(Date startDate, Date endDate) {

        Calendar c = Calendar.getInstance();

        c.setTime(startDate);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long l1 = c.getTimeInMillis();

        c.setTime(endDate);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long l2 = c.getTimeInMillis();

        long days = (l2 - l1) / (1000 * 60 * 60 * 24);//化为天

        return days;
    }

    public static Date[] getAboutDateBy0to23Hour(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + day);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date hour0 = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date hour23 = calendar.getTime();

        return new Date[]{hour0, hour23};
    }

    /**
     * 返回当前时间按下列参数进行增量后的对应时间点
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getDateIncreasedBy(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        if (year != 0) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year);
        }
        if (month != 0) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + month);
        }
        if (day != 0) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + day);
        }
        if (hour != 0) {
            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + hour);
        }
        if (minute != 0) {
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minute);
        }
        if (second != 0) {
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + second);
        }

        return calendar.getTime();
    }

    public static Date getYesterday() throws ParseException {
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
        return date.getTime();
    }

}

