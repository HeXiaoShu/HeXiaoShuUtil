package com.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author hexiaoshu
 * @Description:  常用时间工具类
 */
public class DateUtil {

    // df.parse() 转 Date类型
    /**
     * 获取当前时间
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String nowDateAll() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * 获取当前时间
     * @return yyyy-MM-dd
     */
    public static String nowDateYmd() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    /**
     * 获取当前时间
     * @return HH:mm:ss
     */
    public static String nowDateHms() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * 获取当前时间,无格式存数字
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String nowDateAllNumber() {
        return nowDateAll().replace("-", "").replace(":", "").replace(" ","");
    }

    /**
     * 获取当前时间,无格式存数字
     * @return yyyy-MM-dd
     */
    public static String nowDateYmdNumber() {
        return nowDateYmd().replace("-", "").replace(" ","");
    }

    /**
     * 获取 前后x天,无格式存数字时间
     * @return yyyy-MM-dd
     */
    public static String beforeOrOldDateNumber(Integer count) {
        return beforeOrOldDate(count).replace("-", "").replace(" ","");
    }

    /**
     * 获取当前时间
     * @return xx-年-xx-月-xx-日
     */
    public static String nowDateChinese() {
        StringBuilder sb = new StringBuilder(nowDateYmd());
        StringBuilder replace = sb.replace(4, 5, "年").replace(7, 8, "月").append("日");
        return replace.toString();
    }

    /**
     * 获取当前时间,冒号格式
     * @return HH:mm:ss
     */
    public static String nowDateColon() {
        StringBuilder sb = new StringBuilder(nowDateAll());
        StringBuilder replace = sb.replace(13, 14, ":").replace(16, 17, ":");
        return replace.toString();
    }

    /**
     * 获取当前时间的，前,或，后几天
     * @param num 负数为 前.
     * @return yyyy-MM-dd
     */
    public static String beforeOrOldDate(Integer num){
        Date date=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        date = calendar.getTime();
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 获取当前时间的， 毫秒级时间戳
     * @return 时间戳
     */
    public static Long milliTimestamp(){
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间的， 秒级时间戳
     * @return 时间戳
     */
    public static Long secondTimestamp(){
        return System.currentTimeMillis() / 1000;
    }
    
    /**
     * 获取本月第一天
     * @return String
     */
    public static String firstDayOfMonth(){
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime firstday = date.with(TemporalAdjusters.firstDayOfMonth());
        return firstday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 获取本月最后一天
     * @return String
     */
    public static String lastDayOfMonth(){
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 获取两个long型 日期 之间间隔天数
     * @param start
     * @param end
     * @return
     */
    public static long longDaysBetween(Long start,Long end){
        LocalDate startDate =LocalDate.parse(String.valueOf(start), DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate endDate = LocalDate.parse(String.valueOf(end), DateTimeFormatter.ofPattern("yyyyMMdd"));
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 获取两日期之间间隔的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     */
    public static int daysBetween(Date smdate, Date bdate) {
        long between_days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            between_days = (time2 - time1) / (1000 * 3600 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(String.valueOf(between_days));
    }

    public static Date parse(String date, String format) {

        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
