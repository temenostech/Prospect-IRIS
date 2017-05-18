package com.temenos.marketplace;

/**
 * TODO: Document me!
 *
 * @author jfiricel
 *
 */
public class TestCustomerInfoITCase {
    public static final String HTTP_METHOD_GET = "GET"; 
    
    public static final String HTTP_HEADER_ACCEPT = "Accept";
    
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    
    private static String resourceName = "enqCustomerInfos()";
    /*
    @Test
    public void testCustomerInfo1() throws UnirestException, IOException {
        //filter all the records for which the Nationality is containing "US" value
        String resourceFilter = "?$filter=substringof('US', Nationality)";
        String url = Configuration.DATA_SERVICE_URL + resourceName + resourceFilter;
        Unirest.setDefaultHeader(HTTP_HEADER_ACCEPT, APPLICATION_ATOM_XML);
        HttpResponse<InputStream> response = Unirest.get(url)
                .basicAuth(Configuration.INPUTTER_USER_NAME, Configuration.INPUTTER_PASSWORD).asBinary();
        String resultStr = IOUtils.toString(response.getBody());
        System.out.println(resultStr);
        String resultToMatch = "(?:\\<d\\:Natlty\\>(?!US)\\<\\/d\\:Natlty\\>)*?";
        System.out.println(resultToMatch);
        assertFalse(resultStr.matches(resultToMatch));
    }
    */
}
