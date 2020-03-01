package com.gregorriegler.breakdependencies;public class Statistics{	public Statistics()	{	}protected double[] fetchMeanListBetter(java.time.YearMonth begin, java.time.YearMonth end) {
        double[] meanList = {};
        try {
            java.net.URL url = new java.net.URL("https://api.meteostat.net/v1/history/monthly?station=11035&start=" + begin + "&end=+" + end + "&key=" + java.lang.System.getProperty("key"));
            meanList = com.jayway.jsonpath.JsonPath.parse(url).read("$.data[*].temperature_mean", double[].class);
        } catch (java.lang.Exception e) {
            com.gregorriegler.breakdependencies.MeanTemperatureApplication.LOG.error("an error occured", e);
        }
        return meanList;
    }}