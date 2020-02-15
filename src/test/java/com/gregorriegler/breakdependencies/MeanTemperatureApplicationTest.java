package com.gregorriegler.breakdependencies;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

class MeanTemperatureApplicationTest {

    @Test
    void should_run() {
        MeanTemperatureApplication application = new MeanTemperatureApplication();
        application.printMeans(YearMonth.of(2020, 2));
    }
}