package xyz.ufactions.customcrates.libs;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UtilTime {
    public static final String DATE_FORMAT_NOW = "MM-dd-yyyy HH:mm:ss";

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(millis);
        long hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        String format = "";
        if (days != 0) {
            format = "%d days";
        }
        if (hours != 0) {
            if (format.isEmpty()) {
                format = "%h hours";
            } else {
                format += " %h hours";
            }
        }
        if (minutes != 0) {
            if (format.isEmpty()) {
                format = "%m minutes";
            } else {
                format += " %m minutes";
            }
        }
        if (format.isEmpty()) {
            format = "%s seconds";
        } else {
            if (seconds != 0) {
                format += " %s seconds";
            }
        }
        return format.replaceAll("%d", String.valueOf(days)).replaceAll("%h", String.valueOf(hours)).replaceAll("%m", String.valueOf(minutes)).replaceAll("%s", String.valueOf(seconds));
    }

    public static boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }
}