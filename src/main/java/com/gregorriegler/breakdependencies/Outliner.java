package com.gregorriegler.breakdependencies;

import java.time.YearMonth;

public class Outliner {
    public Outliner() {
    }

    public void outline(YearMonth begin, YearMonth end, double mean) {
        ThirdPartyUtil.outline(begin, end, mean);
    }
}