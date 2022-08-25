package com.error504.baf;

import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;

public class Time {

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
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

        if (diffTime < TIME_MAXIMUM.SEC) {
            return diffTime + "초전";
        }

        diffTime = diffTime / TIME_MAXIMUM.SEC;
        if (diffTime < TIME_MAXIMUM.MIN) {
            return diffTime + "분 전";
        }
        diffTime = diffTime / TIME_MAXIMUM.MIN;

        if (diffTime < TIME_MAXIMUM.HOUR) {
            return diffTime + "시간 전";
        }

        diffTime = diffTime / TIME_MAXIMUM.HOUR;

        if (diffTime < TIME_MAXIMUM.DAY) {
            return diffTime + "일 전";
        }

        diffTime = diffTime / TIME_MAXIMUM.DAY;

        if (diffTime < TIME_MAXIMUM.MONTH) {
            return diffTime + "개월 전";
        }

        diffTime = diffTime / TIME_MAXIMUM.MONTH;
        return diffTime + "년 전";
    }

}