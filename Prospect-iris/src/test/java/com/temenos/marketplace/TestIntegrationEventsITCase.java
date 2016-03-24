package com.temenos.marketplace;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * GET integration events using unirest client library
 * 
 * @author mjangid
 *
 */
public class TestIntegrationEventsITCase {

    private static String resourceName = "enqIntegrationEventss()";

    @Test
    public void testIntegrationEvents() throws UnirestException, IOException {
        String resourceFilter = "?$filter=substringof('MarketPlace', EventType)";
        String url = Configuration.DATA_SERVICE_URL + resourceName + resourceFilter;
        Unirest.setDefaultHeader("Accept", "application/atom+xml");
        HttpResponse<InputStream> response = Unirest.get(url)
                .basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD).asBinary();
        System.out.println(IOUtils.toString(response.getBody()));
    }

    @Test
    public void testGetEventsCreationTimeEQ() throws UnirestException, IOException, ParseException {

        // set the date you want to fetch the events since
        Date sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2016-03-23 20:38:32.522");
        long time = sd.getTime();
        // format the timestamp so T24 understand
        String t24Date = String.valueOf(time).substring(0, 10) + "." + String.valueOf(time).substring(10);

        String filter = "?$filter=substringof('MarketPlace', EventType)&$filter=CreationTime eq '" + t24Date + "'";
        String url = Configuration.DATA_SERVICE_URL + resourceName + filter;
        Unirest.setDefaultHeader("Accept", "application/atom+xml");
        HttpResponse<InputStream> response = Unirest.get(url)
                .basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD).asBinary();
        System.out.println(IOUtils.toString(response.getBody()));
    }

    @Test
    // use gt odata filter
    public void testGetEventsCreationTimeSince() throws UnirestException, IOException, ParseException {

        // set the date you want to fetch the events since
        Date sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2016-03-23 20:38:32.522");
        long time = sd.getTime();
        // format the time stamp so T24 understand
        String t24Date = String.valueOf(time).substring(0, 10) + "." + String.valueOf(time).substring(10);

        String filter = "?$filter=substringof('MarketPlace', EventType)&$filter=CreationTime gt '" + t24Date + "'";
        String url = Configuration.DATA_SERVICE_URL + resourceName + filter;
        Unirest.setDefaultHeader("Accept", "application/atom+xml");
        HttpResponse<InputStream> response = Unirest.get(url)
                .basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD).asBinary();
        System.out.println(IOUtils.toString(response.getBody()));
    }

    @Test
    // use gt odata filter
    public void testGetEventsTimeRange() throws UnirestException, IOException, ParseException {

        // set the date you want to fetch the events since - keep one hour gap
        Date sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2016-03-23 19:38:32.522");
        Date sd2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2016-03-23 20:38:32.522");
        long time1 = sd1.getTime();
        long time2 = sd2.getTime();

        // format the time stamp so T24 understand
        String t24Date1 = String.valueOf(time1).substring(0, 10) + "." + String.valueOf(time1).substring(10);
        String t24Date2 = String.valueOf(time2).substring(0, 10) + "." + String.valueOf(time2).substring(10);

        //create filter string
        String filter = "?$filter=substringof('MarketPlace', EventType)&$filter=CreationTime gt '" + t24Date1
                + "' and CreationTime gt '" + t24Date2 + "'";
        
        String url = Configuration.DATA_SERVICE_URL + resourceName + filter;
        Unirest.setDefaultHeader("Accept", "application/atom+xml");
        HttpResponse<InputStream> response = Unirest.get(url)
                .basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD).asBinary();
        System.out.println(IOUtils.toString(response.getBody()));
    }
}
