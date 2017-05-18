package com.temenos.marketplace;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Tests for the line of code "GET -> T24.verCardIssue.verCardIssues" from Prospect.rim
 * 
 * @author jfiricel
 *
 */
public class TestCardIssueITCase {
    
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_GET = "GET"; 
    public static final String HTTP_METHOD_DELETE = "DELETE";
    
    public static final String HTTP_HEADER_ACCEPT = "Accept";
    
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    /*
    private static String resourceName = "verCardIssues()";

    @Test
    public void testCardIssue1() throws IOException, UnirestException {
        String resourceFilter = "?$filter=substringof('Prospect', EventType)";
        String url = Configuration.DATA_SERVICE_URL + resourceName + resourceFilter;
        System.out.println("url = ");
        System.out.println(url);
        Unirest.setDefaultHeader(HTTP_HEADER_ACCEPT, APPLICATION_ATOM_XML);
        HttpResponse<InputStream> response = Unirest.get(url)
                .basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD).asBinary();
        System.out.println(IOUtils.toString(response.getBody()));
    }
    */
}