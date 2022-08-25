package com.error504.baf;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class SetTime {

    private static class TimeMaximum{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static int getWeekOfYear(String date) {
        Calendar calendar = Calendar.getInstance();
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);
        calendar.set(year, month - 1, day);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static String convertLocaldatetimeToTime(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        long diffTime = localDateTime.until(now, ChronoUnit.SECONDS);
        // now보다 이후면 +, 전이면 -

        if (diffTime < TimeMaximum.SEC) {
            return diffTime + "초전";
        }

        diffTime = diffTime / TimeMaximum.SEC;
        if (diffTime < TimeMaximum.MIN) {
            return diffTime + "분 전";
        }
        diffTime = diffTime / TimeMaximum.MIN;

        if (diffTime < TimeMaximum.HOUR) {
            return diffTime + "시간 전";
        }

        diffTime = diffTime / TimeMaximum.HOUR;

        if (diffTime < TimeMaximum.DAY) {
            return diffTime + "일 전";
        }

        diffTime = diffTime / TimeMaximum.DAY;

        if (diffTime < TimeMaximum.MONTH) {
            return diffTime + "개월 전";
        }

        diffTime = diffTime / TimeMaximum.MONTH;
        return diffTime + "년 전";
    }

}