// FILE: src/main/java/com/mobilewallet/util/DateUtil.java
package com.mobilewallet.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String formatISO(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_FORMATTER);
    }

    public static String formatDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static LocalDateTime parseISO(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, ISO_FORMATTER);
    }

    public static boolean isExpired(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(dateTime);
    }
}