package com.gregorriegler.breakdependencies;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Clock;
import java.time.YearMonth;

/**
 * Prints the mean temperatures of 3 recent months measured in Vienna over the last 50 years for comparison.
 *
 * Example Output:
 * 1969-09 to 1969-12 mean temperature: 7.7 °C
 * 1970-09 to 1970-12 mean temperature: 8.2 °C
 * 1971-09 to 1971-12 mean temperature: 7.9 °C
 * 1972-09 to 1972-12 mean temperature: 6.5 °C
 * 1973-09 to 1973-12 mean temperature: 7.4 °C
 * 1974-09 to 1974-12 mean temperature: 7.9 °C
 * ...
 * 2014-09 to 2014-12 mean temperature: 10.0 °C
 * 2015-09 to 2015-12 mean temperature: 9.7 °C
 * 2016-09 to 2016-12 mean temperature: 8.6 °C
 * 2017-09 to 2017-12 mean temperature: 9.1 °C
 * 2018-09 to 2018-12 mean temperature: 10.0 °C
 * 2019-09 to 2019-12 mean temperature: 10.1 °C
 */
public class MeanTemperatureApplication {
    private static final Logger LOG = LoggerFactory.getLogger(MeanTemperatureApplication.class);
    private final Outliner outliner;

    public MeanTemperatureApplication(Outliner outliner) {
        this.outliner = outliner;
    }

    public static void main(String[] args) {
        MeanTemperatureApplication application = new MeanTemperatureApplication(new Outliner());
        application.printHistoricalMeanTemperatures(Clock.systemDefaultZone());
    }

    public void printHistoricalMeanTemperatures(Clock clock) {
        YearMonth lastMonth = YearMonth.now(clock).minusMonths(2); // the climate api might not yet have data for the last month

        for (int year = lastMonth.getYear() - 50; year <= lastMonth.getYear(); year++) {
            double sum = 0;
            long count = 0;

            YearMonth begin = lastMonth.withYear(year).minusMonths(2);
            YearMonth end = lastMonth.withYear(year);

            try {
                URL url = new URL("https://api.meteostat.net/v1/history/monthly?station=11035&start=" + begin + "&end=+" + end + "&key=" + System.getProperty("key"));
                double[] meanList = JsonPath.parse(url).read("$.data[*].temperature_mean", double[].class);

                for (int i = 0; i < meanList.length; i++) {
                    double mean = meanList[i];
                    sum += mean;
                    count++;
                }

                if (count > 0) {
                    double mean = sum / count;
                    outliner.outline(begin, end, mean);
                }
            } catch (Exception e) {
                LOG.error("an error occured", e);
            }
        }
    }

}
