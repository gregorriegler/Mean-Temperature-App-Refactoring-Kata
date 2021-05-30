package com.gregorriegler.breakdependencies;

import java.time.YearMonth;

/**
 * This is some third party util you are not allowed to change
 */
public class ThirdPartyService {

    public ThirdPartyService() {
        throw new RuntimeException("failed to connect to third party");
    }

    public void outline(YearMonth end, YearMonth begin, double mean) {
        System.out.println(begin + " to " + end + " mean temperature: " + Math.round(mean * 10) / 10D + " °C");
    }
}
