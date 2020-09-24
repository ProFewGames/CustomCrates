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

    public static boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }
}