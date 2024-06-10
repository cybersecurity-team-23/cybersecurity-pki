package com.example.pki.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateConverter {
    private static final ZoneId currentTimeZone = ZoneId.systemDefault();

    public static long getCurrentUnixTime() {
        return LocalDateTime.now().atZone(currentTimeZone).toInstant().getEpochSecond();
    }

    public static long getCurrentUnixTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long convertToUnixTime(Date date) {
        return date.toInstant().atZone(currentTimeZone).toInstant().getEpochSecond();
    }
}
