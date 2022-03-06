package cn.edu.xmu.oomall.activity.constant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeFormat {
    public static final String INPUT_DATE_TIME_FORMAT = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String OUTPUT_DATE_TIME_FORMAT = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ";
    public static final String TIME_ZONE="GMT+8";
    public static LocalDateTime ZonedDateTime2LocalDateTime(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        } else {
            return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        }
    }
}
