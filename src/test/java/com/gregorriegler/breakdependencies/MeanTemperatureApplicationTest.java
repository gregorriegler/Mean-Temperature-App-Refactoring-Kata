package com.gregorriegler.breakdependencies;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MeanTemperatureApplicationTest {

    @Test
    void should_run() {
        List<Object[]> ranges = new ArrayList<>();
        List<Object[]> prints = new ArrayList<>();

        MeanTemperatureApplication application = new MeanTemperatureApplication() {
            @Override
            protected double[] fetchMeanList(YearMonth begin, YearMonth end) {
                ranges.add(new Object[]{begin, end});
                return new double[]{1d, 2d, 3d, 4d};
            }

            @Override
            protected void print(YearMonth begin, YearMonth end, double avg) {
                prints.add(new Object[]{begin, end, avg});
            }
        };

        application.printMeans(YearMonth.of(2020, 2));

        assertThat(ranges).hasSize(51)
            .contains(new Object[]{YearMonth.of(1969, 9), YearMonth.of(1969, 12)})
            .contains(new Object[]{YearMonth.of(2000, 9), YearMonth.of(2000, 12)})
            .contains(new Object[]{YearMonth.of(2019, 9), YearMonth.of(2019, 12)});

        assertThat(prints).hasSize(51)
            .contains(new Object[]{YearMonth.of(1969, 9), YearMonth.of(1969, 12), 2.5d})
            .contains(new Object[]{YearMonth.of(2000, 9), YearMonth.of(2000, 12), 2.5d})
            .contains(new Object[]{YearMonth.of(2019, 9), YearMonth.of(2019, 12), 2.5d});
    }

    @Test
    void should_not_print_empty_result() {
        List<Object[]> ranges = new ArrayList<>();
        List<Object[]> prints = new ArrayList<>();

        MeanTemperatureApplication application = new MeanTemperatureApplication() {
            @Override
            protected double[] fetchMeanList(YearMonth begin, YearMonth end) {
                ranges.add(new Object[]{begin, end});
                return new double[]{};
            }

            @Override
            protected void print(YearMonth begin, YearMonth end, double avg) {
                prints.add(new Object[]{begin, end, avg});
            }
        };

        application.printMeans(YearMonth.of(2020, 2));

        assertThat(prints).isEmpty();
    }

    @Test
    void should_log_error() {
        Appender appender = mock(Appender.class);
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MeanTemperatureApplication.class);
        logger.getLoggerContext().getLogger("ROOT").detachAndStopAllAppenders();
        logger.addAppender(appender);

        MeanTemperatureApplication application = new MeanTemperatureApplication() {
            @Override
            protected double[] fetchMeanList(YearMonth begin, YearMonth end) throws IOException {
                throw new IOException("yep");
            }
        };

        application.printMeans(YearMonth.of(2020, 2));

        verify(appender, times(51)).doAppend(argThat(
            o -> ((ILoggingEvent) o).getFormattedMessage().equals("an error occured"))
        );
    }
}