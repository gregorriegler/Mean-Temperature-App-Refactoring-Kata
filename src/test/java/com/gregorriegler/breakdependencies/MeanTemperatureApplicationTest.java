package com.gregorriegler.breakdependencies;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

class MeanTemperatureApplicationTest {

    @Test
    void should_print_temperatures() {
        Outliner outliner = Mockito.mock(Outliner.class);
        MeanTemperatureApplication application = new MeanTemperatureApplication(outliner);
        application.printHistoricalMeanTemperatures(Clock.fixed(Instant.parse("2020-05-20T00:00:00Z"), ZoneOffset.UTC));
    }
}