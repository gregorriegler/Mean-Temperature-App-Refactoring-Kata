package com.gregorriegler.breakdependencies;

import java.time.YearMonth;

/**
 * This is some third party util you are not allowed to change
 */
public class ThirdPartyUtil {

    private ThirdPartyUtil() {
    }

    public static void outline(YearMonth end, YearMonth begin, double mean) {
        System.out.println(begin + " to " + end + " mean temperature: " + Math.round(mean * 10) / 10D + " °C");
    }
}
