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

    private final List<Object[]> fetchedMeans = new ArrayList<>();
    private final List<Object[]> printInvocations = new ArrayList<>();

    @Test
    void should_fetch_means_for_calculated_months_and_print_them() {
        MeanTemperatureApplication application = new MeanTemperatureApplicationTss(new double[]{1d, 2d, 3d, 4d}, fetchedMeans, printInvocations);

        application.printMeans(YearMonth.of(2020, 2));

        assertThat(fetchedMeans).hasSize(51)
            .contains(new Object[]{YearMonth.of(1969, 9), YearMonth.of(1969, 12)})
            .contains(new Object[]{YearMonth.of(2000, 9), YearMonth.of(2000, 12)})
            .contains(new Object[]{YearMonth.of(2019, 9), YearMonth.of(2019, 12)});

        assertThat(printInvocations).hasSize(51)
            .contains(new Object[]{YearMonth.of(1969, 9), YearMonth.of(1969, 12), 2.5d})
            .contains(new Object[]{YearMonth.of(2000, 9), YearMonth.of(2000, 12), 2.5d})
            .contains(new Object[]{YearMonth.of(2019, 9), YearMonth.of(2019, 12), 2.5d});
    }

    @Test
    void should_not_print_empty_result() {
        MeanTemperatureApplication application = new MeanTemperatureApplicationTss(new double[]{}, fetchedMeans, printInvocations);

        application.printMeans(YearMonth.of(2020, 2));

        assertThat(printInvocations).isEmpty();
    }

    @Test
    void should_log_error() {
        Appender appender = mockLogAppender();

        MeanTemperatureApplication application = new MeanTemperatureApplication() {
            protected double[] fetchMeanList(MonthRange monthRange) throws IOException {
                throw new IOException("yep");
            }
        };

        application.printMeans(YearMonth.of(2020, 2));

        verify(appender, times(51)).doAppend(logMessage("an error occured"));
    }

    private Appender mockLogAppender() {
        Appender appender = mock(Appender.class);
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MeanTemperatureApplication.class);
        logger.getLoggerContext().getLogger("ROOT").detachAndStopAllAppenders();
        logger.addAppender(appender);
        return appender;
    }

    private Object logMessage(String message) {
        return argThat(o -> ((ILoggingEvent) o).getFormattedMessage().equals(message));
    }

    private static class MeanTemperatureApplicationTss extends MeanTemperatureApplication {
        private final List<Object[]> ranges;
        private final double[] meanList;
        private final List<Object[]> prints;

        public MeanTemperatureApplicationTss(double[] meanList, List<Object[]> ranges, List<Object[]> prints) {
            this.ranges = ranges;
            this.meanList = meanList;
            this.prints = prints;
        }

        @Override
        protected double[] fetchMeanList(MonthRange monthRange) throws IOException {
            ranges.add(new Object[]{monthRange.first, monthRange.last});
            return meanList;
        }

        @Override
        protected void print(YearMonth begin, YearMonth end, double avg) {
            prints.add(new Object[]{begin, end, avg});
        }
    }
}