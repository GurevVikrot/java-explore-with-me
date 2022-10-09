package ru.explore.with.me.statistic.util;

import java.time.format.DateTimeFormatter;

public class CustomTimeFormatter {
    public static DateTimeFormatter getFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static String getStingFormat() {
        return "yyyy-MM-dd HH:mm:ss";
    }
}
