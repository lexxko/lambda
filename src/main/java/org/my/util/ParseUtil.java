package org.my.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.function.Supplier;

public final class ParseUtil {
    public static final Logger log = LoggerFactory.getLogger(ParseUtil.class);
    private ParseUtil() {
    }

    public static LocalDate parseDate(String date, Supplier<LocalDate> fallbackValueSupplier) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeException ex) {
            log.warn("Fallback activated: "+ ex);
            return fallbackValueSupplier.get();
        }
    }
}
