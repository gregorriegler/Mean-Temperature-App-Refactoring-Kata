package com.gregorriegler.breakdependencies;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

class MeanTemperatureApplicationTest {

    @Test
    void should_print_temperatures() {
        MeanTemperatureApplication application = new MeanTemperatureApplication();
        application.printHistoricalMeanTemperatures(Clock.fixed(Instant.parse("2020-01-20T00:00:00Z"), ZoneOffset.UTC));
    }
}