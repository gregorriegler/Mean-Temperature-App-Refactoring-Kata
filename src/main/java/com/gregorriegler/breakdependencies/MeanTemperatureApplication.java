package com.gregorriegler.breakdependencies;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.YearMonth;
import java.util.stream.DoubleStream;

/**
 * Prints the mean temperatures of 3 recent months measured in Vienna over the last 50 years for comparison.
 * <p>
 * Example Output:
 * 1969-09 to 1969-12 mean temperature: 7.7 °C
 * 1970-09 to 1970-12 mean temperature: 8.2 °C
 * 1971-09 to 1971-12 mean temperature: 7.9 °C
 * 1972-09 to 1972-12 mean temperature: 6.5 °C
 * 1973-09 to 1973-12 mean temperature: 7.4 °C
 * 1974-09 to 1974-12 mean temperature: 7.9 °C
 * 1975-09 to 1975-12 mean temperature: 7.9 °C
 * 1976-09 to 1976-12 mean temperature: 7.8 °C
 * 1977-09 to 1977-12 mean temperature: 7.8 °C
 * 1978-09 to 1978-12 mean temperature: 7.3 °C
 * 1979-09 to 1979-12 mean temperature: 8.3 °C
 * 1980-09 to 1980-12 mean temperature: 7.2 °C
 * 1981-09 to 1981-12 mean temperature: 7.9 °C
 * 1982-09 to 1982-12 mean temperature: 9.1 °C
 * 1983-09 to 1983-12 mean temperature: 7.7 °C
 * 1984-09 to 1984-12 mean temperature: 8.0 °C
 * 1985-09 to 1985-12 mean temperature: 7.9 °C
 * 1986-09 to 1986-12 mean temperature: 7.8 °C
 * 1987-09 to 1987-12 mean temperature: 9.3 °C
 * 1988-09 to 1988-12 mean temperature: 7.8 °C
 * 1989-09 to 1989-12 mean temperature: 8.1 °C
 * 1990-09 to 1990-12 mean temperature: 7.9 °C
 * 1991-09 to 1991-12 mean temperature: 7.8 °C
 * 1992-09 to 1992-12 mean temperature: 7.7 °C
 * 1993-09 to 1993-12 mean temperature: 7.3 °C
 * 1994-09 to 1994-12 mean temperature: 8.7 °C
 * 1995-09 to 1995-12 mean temperature: 6.9 °C
 * 1996-09 to 1996-12 mean temperature: 7.0 °C
 * 1997-09 to 1997-12 mean temperature: 7.9 °C
 * 1998-09 to 1998-12 mean temperature: 6.8 °C
 * 1999-09 to 1999-12 mean temperature: 8.3 °C
 * 2000-09 to 2000-12 mean temperature: 9.5 °C
 * 2001-09 to 2001-12 mean temperature: 7.3 °C
 * 2002-09 to 2002-12 mean temperature: 7.7 °C
 * 2003-09 to 2003-12 mean temperature: 7.9 °C
 * 2004-09 to 2004-12 mean temperature: 8.5 °C
 * 2005-09 to 2005-12 mean temperature: 8.0 °C
 * 2006-09 to 2006-12 mean temperature: 10.3 °C
 * 2007-09 to 2007-12 mean temperature: 6.8 °C
 * 2008-09 to 2008-12 mean temperature: 8.7 °C
 * 2009-09 to 2009-12 mean temperature: 8.8 °C
 * 2010-09 to 2010-12 mean temperature: 6.8 °C
 * 2011-09 to 2011-12 mean temperature: 8.7 °C
 * 2012-09 to 2012-12 mean temperature: 8.6 °C
 * 2013-09 to 2013-12 mean temperature: 8.9 °C
 * 2014-09 to 2014-12 mean temperature: 10.0 °C
 * 2015-09 to 2015-12 mean temperature: 9.7 °C
 * 2016-09 to 2016-12 mean temperature: 8.6 °C
 * 2017-09 to 2017-12 mean temperature: 9.1 °C
 * 2018-09 to 2018-12 mean temperature: 10.0 °C
 * 2019-09 to 2019-12 mean temperature: 10.1 °C
 */
public class MeanTemperatureApplication {
    private static final Logger LOG = LoggerFactory.getLogger(MeanTemperatureApplication.class);

    public static void main(String[] args) {
        new MeanTemperatureApplication().printMeans(YearMonth.now());
    }

    public void printMeans(YearMonth month) {
        YearMonth lastMonth = month.minusMonths(2); // the climate api might not yet have data for the last month

        for (int year = lastMonth.getYear() - 50; year <= lastMonth.getYear(); year++) {
            Last3Months last3Months = Last3Months.of(lastMonth.withYear(year));

            try {
                DoubleStream.of(fetchMeanList(last3Months.first, last3Months.last)).average().ifPresent(avg -> print(last3Months.first, last3Months.last, avg));
            } catch (Exception e) {
                LOG.error("an error occured");
            }
        }
    }

    protected double[] fetchMeanList(YearMonth begin, YearMonth end) throws IOException {
        URL url = new URL("https://api.meteostat.net/v1/history/monthly?station=11035&start=" + begin + "&end=+" + end + "&key=" + System.getProperty("key"));
        return JsonPath.parse(url).read("$.data[*].temperature_mean", double[].class);
    }

    protected void print(YearMonth begin, YearMonth end, double avg) {
        System.out.println(begin + " to " + end + " mean temperature: " + Math.round(avg * 10) / 10D + " °C");
    }
}

class Last3Months {
    public final YearMonth first;
    public final YearMonth last;

    public static Last3Months of(YearMonth last) {
        return new Last3Months(last);
    }

    private Last3Months(YearMonth last) {
        this.first = last.minusMonths(3);
        this.last = last;
    }
}