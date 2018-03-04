package com.foxpower.flchatofandroid.util.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fengli on 2018/3/2.
 */

public class TimeUtil {


    public static String getHourStrTime(long timestamp) {
        return getStrTime(timestamp, "HH:mm");
    }

    public static String getStrTime (long timestamp, String format) {

        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        timeString = sdf.format(new Date(timestamp));
        return timeString;
    }
}
