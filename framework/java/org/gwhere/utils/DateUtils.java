package org.gwhere.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author dada
 */
public class DateUtils {

    /**
     * <pre>
     *               根据指定的日期格式格式化指定的日期
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               String time = &quot;20080404&quot;;
     *               Date date = DateUtils.parseTime(time, &quot;yyyyMMdd&quot;);
     *
     *               例如时间为20080404，
     *               则转化后为2008年的4月4日。
     *
     * </pre>
     *
     * @param time
     * @param pattern
     * @return
     * @author dada
     */
    public static Date parseTime(String time, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <pre>
     *               根据指定的日期格式格式化指定的日期
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               String str = DateUtils.formatTime(date, &quot;yyyy-MM-dd&quot;);
     *
     *               例如时间为2008年4月4日，
     *               则转化后为2008-04-04。
     *
     *               返回字符串对象为任意指定格式的字符串。
     * </pre>
     *
     * @param date
     * @param pattern
     * @return
     * @author dada
     */
    public static String formatTime(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String formatTime(Date date) {
        return formatTime(date, "yyyy-MM-dd");
    }

    /**
     * <pre>
     *               根据指定的日期格式格式化现在的时间
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               String str = DateUtils.formatCurrTime(&quot;yyyy-MM-dd&quot;);
     *
     *               例如现在为2008年4月4日，
     *               则转化后为2008-04-04。
     *
     *               与formatTime()方法不同的是此方法返回当前事件的格式化字符串。
     * </pre>
     *
     * @param pattern
     * @return
     * @author dada
     */
    public static String formatCurrTime(String pattern) {
        Date date = new Date();
        return formatTime(date, pattern);
    }

    /**
     * <pre>
     *               格式化时间为0分0秒
     *
     *               此方法常用在处理yyyy-MM-dd HH格式的时间上，
     *               使用此方法可以将时间的分和秒都抹去。
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               Date new = DateUtils.parseDate(date);
     *
     *               例如传入时间是2008年4月4日4时44分44秒，
     *               转化后为2008年4月4日4时0分0秒.
     *
     * </pre>
     *
     * @return
     * @author dada
     */
    public static Date parseDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar retval = Calendar.getInstance();
        retval.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return retval.getTime();
    }

    /**
     * <pre>
     *               返回该月可能的最大日期。
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               Date new = DateUtils.actualMaximumDate(date);
     *
     *               例如传入的日期为2008年4月1日，
     *               则返回的日期是2008年4月30日。
     *
     * </pre>
     *
     * @param date
     * @return
     * @author dada
     */
    public static Date actualMaximumDate(Date date) {
        Calendar calendar = calendar(date);
        int actualMaximumDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, actualMaximumDay);
        return calendar.getTime();
    }

    /**
     * <pre>
     *               返回该月可能的最小日期。
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               Date new = DateUtils.actualMinimumDate(date);
     *
     *               例如传入的日期为2008年4月20日，
     *               则返回的日期是2008年4月1日。
     *
     * </pre>
     *
     * @param date
     * @return
     * @author dada
     */
    public static Date actualMinimumDate(Date date) {
        Calendar calendar = calendar(date);
        int actualMininumDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, actualMininumDay);
        return calendar.getTime();
    }

    /**
     * <pre>
     *               返回指定月数前最大的日期。
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               Date new = DateUtils.actualMinimumDate(date, 1);
     *
     *               例如传入的日期为2008年4月20日，
     *               则返回的日期时2008年3月31日。
     *
     * </pre>
     *
     * @param date
     * @param month
     * @return
     * @author dada
     */
    public static Date actualMaximumDate(Date date, int month) {
        Calendar calendar = calendar(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - month);
        int actualMininumDay = calendar
                .getActualMaximum((Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, actualMininumDay);
        return calendar.getTime();
    }

    /**
     * <pre>
     *               返回指定月数前最小的日期。
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               Date new = DateUtils.actualMinimumDate(date, 1);
     *
     *               例如传入的日期为2008年4月20日，
     *               则返回的日期时2008年3月1日。
     *
     * </pre>
     *
     * @param date
     * @param month
     * @return
     * @author dada
     */
    public static Date actualMinimumDate(Date date, int month) {
        Calendar calendar = calendar(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - month);
        int actualMininumDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, actualMininumDay);
        return calendar.getTime();
    }

    /**
     * <pre>
     *               返回给定月的上一个月(默认为上一个月的第一天)
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               Date new = DateUtils.lastMonth(date);
     *
     *               例如传入的日期为2008年4月20日，
     *               则返回的日期时2008年3月1日。
     *
     * </pre>
     *
     * @param date
     * @return
     * @author dada
     */
    public static Date lastMonth(Date date) {
        return actualMinimumDate(date, 1);
    }

    /**
     * <pre>
     *               返回给定月的下一个月(默认为下一个月的最后一天)
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               Date new = DateUtils.nextMonth(date);
     *
     *               例如传入的日期为2008年4月20日，
     *               则返回的日期时2008年5月31日。
     *
     * </pre>
     *
     * @param date
     * @return
     * @author dada
     */
    public static Date nextMonth(Date date) {
        return actualMaximumDate(date, -1);
    }

    /**
     * 获取制定日期的月数差
     *
     * @param startDate
     * @param endDate
     * @return
     * @author dada
     */
    public static int monthBetween(Date startDate, Date endDate) {
        int months = 0;
        Calendar startDay = calendar(startDate);
        Calendar endDay = calendar(endDate);

        int startYear = startDay.get(Calendar.YEAR);
        int startMonth = startDay.get(Calendar.MONTH);

        int endYear = endDay.get(Calendar.YEAR);
        int endMonth = endDay.get(Calendar.MONTH);

        months = (endYear - startYear) * 12 + (endMonth - startMonth);
        return months;
    }

    /**
     * 获取制定日期的日期差
     *
     * @param startDate
     * @param endDate
     * @return
     * @author dada
     */
    public static int dayBetween(Date startDate, Date endDate) {
        return (int) (endDate.getTime() / 86400000 - startDate.getTime() / 86400000);
    }

    /**
     * 获取制定日期的年数差
     *
     * @param startDate
     * @param endDate
     * @return
     * @author dongnw
     */
    public static int yearBetween(Date startDate, Date endDate) {
        String date1 = DateUtils.formatTime(startDate);
        String date2 = DateUtils.formatTime(endDate);
        int n = 0;
        String formatStyle = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(formatStyle);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(date1));
            c2.setTime(df.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while (!c1.after(c2)) { // 循环对比，直到相等，n 就是所要的结果
            n++;
            c1.add(Calendar.DATE, 1); // 比较天数，日期+1
        }
        n = n - 1;
        n = (int) n / 365;
        return n;
    }

    /**
     * 判断日期是否季度末
     *
     * @param date
     * @return
     * @author dada
     */
    public static boolean seasonEnd(Date date) {
        Calendar calendar = calendar(date);

        int month = calendar.get(Calendar.MONTH);
        return (month + 1) % 3 == 0;
    }

    /**
     * 判断日期是否半年末
     *
     * @param date
     * @return
     * @author dada
     */
    public static boolean halfYearEnd(Date date) {
        Calendar calendar = calendar(date);

        int month = calendar.get(Calendar.MONTH);
        return (month + 1) % 6 == 0;
    }

    /**
     * 判断日期是否年末
     *
     * @param date
     * @return
     * @author dada
     */
    public static boolean yearEnd(Date date) {
        Calendar calendar = calendar(date);

        int month = calendar.get(Calendar.MONTH);
        return (month + 1) % 12 == 0;
    }

    /**
     * <pre>
     *               将指定日期转换为相应的Calendar对象
     *
     *               &lt;strong&gt;程序范例：&lt;/strong&gt;
     *               Date date = new Date();
     *               Calendar calendar = DateUtils.calendar(date);
     *
     * </pre>
     *
     * @param date
     * @return
     */
    public static Calendar calendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 格式化日期，去除时分秒，按指定格式处理日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date formatDate(Date date, String pattern) {
        Date formatDate = null;
        if (date != null) {
            String dateStr = formatTime(date);
            formatDate = parseTime(dateStr, pattern);
        }
        return formatDate;
    }

    /**
     * 格式化日期，去除时分秒，只保留yyyy-MM-dd格式
     *
     * @param
     * @return
     */
    public static Date formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    /**
     * 取得给定日期的去年年末日期
     *
     * @param date
     * @return
     * @author shunkai.zhou
     */
    public static Date getLastYear(Date date) {
        Calendar calendar = calendar(date);
        int year = calendar.get(Calendar.YEAR) - 1;
        calendar.set(year, 11, 31);
        return calendar.getTime();
    }

}
