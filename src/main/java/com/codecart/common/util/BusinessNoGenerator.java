package com.codecart.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public final class BusinessNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    private BusinessNoGenerator() {
    }

    public static String nextOrderNo() {
        return nextNo("ORD");
    }

    public static String nextPaymentNo() {
        return nextNo("PAY");
    }

    public static String nextThirdPartyNo() {
        return nextNo("MOCKTXN");
    }

    private static String nextNo(String prefix) {
        int sequence = SEQUENCE.updateAndGet(current -> current >= 999 ? 1 : current + 1);
        return prefix + LocalDateTime.now().format(FORMATTER) + String.format("%03d", sequence);
    }
}
