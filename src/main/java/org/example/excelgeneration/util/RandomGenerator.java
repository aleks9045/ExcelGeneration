package org.example.excelgeneration.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.SplittableRandom;

/**
 * @author Aleksey
 */
public class RandomGenerator {
    private final static SplittableRandom splittableRandom = new SplittableRandom();
//    private final static Random random = new Random();

    public static Long getLong() {
        return splittableRandom.nextLong();
    }

    public static Integer getInteger() {
        return splittableRandom.nextInt();
    }

    public static Integer getInteger(int origin, int bound) {
        return splittableRandom.nextInt(origin, bound);
    }

    public static Float getFloat() {
        return splittableRandom.nextFloat();
    }

    public static Float getFloat(float origin, float bound) {
        return splittableRandom.nextFloat(origin, bound);
    }

    public static Double getDouble() {
        return splittableRandom.nextDouble();
    }

    public static Double getDouble(double origin, double bound) {
        return splittableRandom.nextDouble(origin, bound);
    }

    public static Instant getInstant() {
        return Instant.ofEpochMilli(getLong());
    }

    public static BigDecimal getBigDecimal() {
        return BigDecimal.valueOf(getDouble() * getInteger()).setScale(4, RoundingMode.HALF_UP);
    }

    public static Boolean getBoolean() {
        return splittableRandom.nextBoolean();
    }

    public static String getString(int n) {
        StringBuilder builder = new StringBuilder(n);
        char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for (int i = 0; i < n; ++i) {
            builder.append(chars[getInteger(0, chars.length)]);
        }
        return builder.toString();
    }
}
