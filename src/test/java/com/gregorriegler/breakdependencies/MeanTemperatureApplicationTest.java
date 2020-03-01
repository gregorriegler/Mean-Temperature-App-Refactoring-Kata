package com.gregorriegler.breakdependencies;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class MeanTemperatureApplicationTest {

    @Test
    void should_outline_temperatures() {
        Outliner outliner = Mockito.mock(Outliner.class);
        List<Tuple> fetchMeanListInvocations = new ArrayList<>();
        MeanTemperatureApplication application = new MeanTemperatureApplication(outliner) {
            @Override
            protected double[] fetchMeanList(YearMonth begin, YearMonth end) throws IOException {
                fetchMeanListInvocations.add(tuple(begin, end));
                return new double[]{1d, 2d, 3d};
            }
        };

        application.printHistoricalMeanTemperatures(Clock.fixed(Instant.parse("2020-05-20T00:00:00Z"), ZoneOffset.UTC));

        assertThat(fetchMeanListInvocations).hasSize(51)
            .startsWith(tuple(YearMonth.of(1970,1), YearMonth.of(1970, 3)))
            .endsWith(tuple(YearMonth.of(2020,1), YearMonth.of(2020, 3)));
        verify(outliner, times(51)).outline(any(), any(), eq(2d));
    }

    @Test
    void should_throw_error() {
        Outliner outliner = Mockito.mock(Outliner.class);
        List<Exception> errorsLogged = new ArrayList<>();
        IOException expectedError = new IOException("something");
        MeanTemperatureApplication application = new MeanTemperatureApplication(outliner) {
            @Override
            protected double[] fetchMeanList(YearMonth begin, YearMonth end) throws IOException {
                throw expectedError;
            }

            @Override
            protected void logError(Exception e) {
                errorsLogged.add(e);
            }
        };

        application.printHistoricalMeanTemperatures(Clock.fixed(Instant.parse("2020-05-20T00:00:00Z"), ZoneOffset.UTC));

        verifyNoInteractions(outliner);
        assertThat(errorsLogged).hasSize(51)
            .containsOnly(expectedError);
    }
}